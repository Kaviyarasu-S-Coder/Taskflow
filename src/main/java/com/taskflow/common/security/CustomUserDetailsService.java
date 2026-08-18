package com.taskflow.common.security;

import com.taskflow.modules.iam.adapter.out.persistence.UserEntity;
import com.taskflow.modules.iam.adapter.out.persistence.UserJpaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userRepository;

    public CustomUserDetailsService(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
                .map(ur -> new SimpleGrantedAuthority(ur.getRole().getName()))
                .toList();

        return new User(
                user.getEmail(),
                user.getPasswordHash(),
                "ACTIVE".equalsIgnoreCase(user.getStatus()),
                true,
                true,
                user.getLockExpiry() == null || user.getLockExpiry().isBefore(java.time.LocalDateTime.now()),
                authorities
        );
    }
}
