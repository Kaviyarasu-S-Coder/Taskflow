package com.taskflow.modules.iam.adapter.out.persistence;

import com.taskflow.modules.iam.domain.User;
import com.taskflow.modules.iam.domain.UserRepository;
import com.taskflow.modules.iam.domain.UserStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Driven adapter — implements the domain's {@link UserRepository} port
 * by delegating to {@link UserJpaRepository}.
 *
 * Responsible for mapping between the domain {@link User} aggregate
 * and the JPA {@link UserEntity}.
 */
@Component
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserPersistenceAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmailAndDeletedFalse(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmailAndDeletedFalse(email);
    }

    // ── Mapping ───────────────────────────────────────────────────────────────

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        if (user.getId() != null) {
            entity.setId(user.getId());
        }
        entity.setUserCode(user.getUserCode());
        entity.setOrganizationId(user.getOrganizationId());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setAvatarUrl(user.getAvatarUrl());
        entity.setStatus(user.getStatus() != null ? user.getStatus().name() : UserStatus.ACTIVE.name());
        entity.setFailedLoginAttempts(user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0);
        entity.setLockExpiry(user.getLockExpiry());
        return entity;
    }

    private User toDomain(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setUserCode(entity.getUserCode());
        user.setOrganizationId(entity.getOrganizationId());
        user.setEmail(entity.getEmail());
        user.setPasswordHash(entity.getPasswordHash());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setAvatarUrl(entity.getAvatarUrl());
        user.setStatus(parseStatus(entity.getStatus()));
        user.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        user.setLockExpiry(entity.getLockExpiry());
        return user;
    }

    private UserStatus parseStatus(String status) {
        try {
            return UserStatus.valueOf(status);
        } catch (IllegalArgumentException | NullPointerException e) {
            return UserStatus.ACTIVE;
        }
    }
}
