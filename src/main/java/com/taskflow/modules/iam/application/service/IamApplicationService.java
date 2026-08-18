package com.taskflow.modules.iam.application.service;

import com.taskflow.common.exception.BusinessRuleViolationException;
import com.taskflow.common.exception.ResourceNotFoundException;
import com.taskflow.common.security.JwtTokenProvider;
import com.taskflow.modules.iam.adapter.out.persistence.*;
import com.taskflow.modules.iam.application.dto.AuthResponse;
import com.taskflow.modules.iam.application.port.in.*;
import com.taskflow.modules.iam.domain.User;
import com.taskflow.modules.iam.domain.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.UUID;

/**
 * Central application service implementing all IAM use cases:
 * registration, authentication, and token refresh.
 */
@Service
@Transactional
public class IamApplicationService
        implements RegisterUserUseCase, AuthenticateUserUseCase, RefreshTokenUseCase {

    private static final String DEFAULT_ROLE = "ROLE_DEVELOPER";

    private final UserRepository userRepository;
    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${taskflow.jwt.expiration-ms}")
    private long accessTokenExpirationMs;

    public IamApplicationService(UserRepository userRepository,
                                  UserJpaRepository userJpaRepository,
                                  RoleJpaRepository roleJpaRepository,
                                  OrganizationJpaRepository organizationJpaRepository,
                                  PasswordEncoder passwordEncoder,
                                  JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
        this.organizationJpaRepository = organizationJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ── Register ──────────────────────────────────────────────────────────────

    @Override
    public AuthResponse register(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new BusinessRuleViolationException(
                    "An account with email '" + command.email() + "' already exists.");
        }

        // Create or reuse an organization for the provided name
        OrganizationEntity org = findOrCreateOrganization(command.organizationName());

        // Build domain user
        String userCode = "USR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        User user = User.create(
                userCode,
                org.getId(),
                command.email(),
                passwordEncoder.encode(command.password()),
                command.firstName(),
                command.lastName()
        );
        User saved = userRepository.save(user);

        // Assign default role via JPA (role entity already seeded by migration)
        assignDefaultRole(saved.getId());

        // Reload with roles for the response
        UserEntity userEntity = userJpaRepository.findByEmailWithRoles(command.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found after registration"));

        List<String> roles = extractRoleNames(userEntity);
        String accessToken = jwtTokenProvider.generateAccessToken(saved.getEmail(), saved.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(saved.getEmail());

        return AuthResponse.of(accessToken, refreshToken, accessTokenExpirationMs,
                saved.getId(), saved.getEmail(), saved.getFirstName(), saved.getLastName(), roles);
    }

    // ── Authenticate (Login) ──────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public AuthResponse authenticate(AuthenticateCommand command) {
        UserEntity userEntity = userJpaRepository.findByEmailWithRoles(command.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(command.password(), userEntity.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!"ACTIVE".equalsIgnoreCase(userEntity.getStatus())) {
            throw new BusinessRuleViolationException(
                    "Account is not active. Current status: " + userEntity.getStatus());
        }

        List<String> roles = extractRoleNames(userEntity);
        String accessToken = jwtTokenProvider.generateAccessToken(userEntity.getEmail(), userEntity.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userEntity.getEmail());

        return AuthResponse.of(accessToken, refreshToken, accessTokenExpirationMs,
                userEntity.getId(), userEntity.getEmail(),
                userEntity.getFirstName(), userEntity.getLastName(), roles);
    }

    // ── Refresh Token ─────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessRuleViolationException("Refresh token is invalid or expired");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        UserEntity userEntity = userJpaRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        List<String> roles = extractRoleNames(userEntity);
        String newAccessToken = jwtTokenProvider.generateAccessToken(userEntity.getEmail(), userEntity.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userEntity.getEmail());

        return AuthResponse.of(newAccessToken, newRefreshToken, accessTokenExpirationMs,
                userEntity.getId(), userEntity.getEmail(),
                userEntity.getFirstName(), userEntity.getLastName(), roles);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private OrganizationEntity findOrCreateOrganization(String orgName) {
        String slug = slugify(orgName);
        return organizationJpaRepository.findAll().stream()
                .filter(o -> o.getSlug().equals(slug))
                .findFirst()
                .orElseGet(() -> {
                    OrganizationEntity org = new OrganizationEntity();
                    org.setName(orgName);
                    org.setSlug(slug);
                    org.setOrganizationCode("ORG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    org.setStatus("ACTIVE");
                    return organizationJpaRepository.save(org);
                });
    }

    private void assignDefaultRole(Long userId) {
        RoleEntity role = roleJpaRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Default role '" + DEFAULT_ROLE + "' not found. Check migration V1."));

        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        UserRoleEntity userRole = new UserRoleEntity(userEntity, role);
        userEntity.getUserRoles().add(userRole);
        userJpaRepository.save(userEntity);
    }

    private List<String> extractRoleNames(UserEntity userEntity) {
        return userEntity.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .toList();
    }

    private String slugify(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
