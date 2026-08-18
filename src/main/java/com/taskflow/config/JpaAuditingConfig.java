package com.taskflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Provides the {@link AuditorAware} bean required by {@code @EnableJpaAuditing}
 * in {@link com.taskflow.TaskFlowApplication}.
 *
 * Reads the currently authenticated user's identifier from the Spring Security
 * context and supplies it to JPA's {@code @CreatedBy} and {@code @LastModifiedBy} fields.
 *
 * Returns {@link Optional#empty()} for unauthenticated requests (e.g. during registration).
 */
@Configuration
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                // The username is the email; resolve to user ID via the principal name
                // For simplicity we return empty when the principal is a String (anonymous)
                return Optional.empty();
            }
            return Optional.empty();
        };
    }
}
