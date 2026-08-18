package com.taskflow.modules.iam.domain;

import java.util.Optional;

/**
 * Outbound port — the domain's contract for user persistence.
 * The JPA adapter in the persistence package implements this interface.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
