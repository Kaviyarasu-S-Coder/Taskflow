package com.taskflow.config;

import com.taskflow.modules.iam.adapter.out.persistence.RoleEntity;
import com.taskflow.modules.iam.adapter.out.persistence.RoleJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Ensures system default roles are present in the database on application startup.
 * Acts as a fallback for environments where Flyway is disabled (e.g. local / test profiles with H2).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleJpaRepository roleJpaRepository;

    public DataInitializer(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initRoles();
    }

    private void initRoles() {
        List<RoleSeed> defaultRoles = List.of(
                new RoleSeed("ROLE_SUPER_ADMIN", "Global System Administrator"),
                new RoleSeed("ROLE_ORG_ADMIN", "Organization Tenant Administrator"),
                new RoleSeed("ROLE_PROJECT_MANAGER", "Project Manager"),
                new RoleSeed("ROLE_DEVELOPER", "Team Member / Developer"),
                new RoleSeed("ROLE_VIEWER", "Read-Only Viewer")
        );

        for (RoleSeed seed : defaultRoles) {
            if (roleJpaRepository.findByName(seed.name()).isEmpty()) {
                RoleEntity role = new RoleEntity();
                role.setName(seed.name());
                role.setDescription(seed.description());
                roleJpaRepository.save(role);
                log.info("Initialized default role: {}", seed.name());
            }
        }
    }

    private record RoleSeed(String name, String description) {}
}
