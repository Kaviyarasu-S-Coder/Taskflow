package com.taskflow.modules.iam.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Eagerly fetches user with roles and role details in a single query.
     * Used by {@code CustomUserDetailsService} to build Spring Security's {@code UserDetails}.
     */
    @Query("""
            SELECT u FROM UserEntity u
            LEFT JOIN FETCH u.userRoles ur
            LEFT JOIN FETCH ur.role
            WHERE u.email = :email
              AND u.deleted = false
            """)
    Optional<UserEntity> findByEmailWithRoles(@Param("email") String email);

    Optional<UserEntity> findByEmailAndDeletedFalse(String email);

    boolean existsByEmailAndDeletedFalse(String email);
}
