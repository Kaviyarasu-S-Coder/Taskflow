package com.taskflow.modules.iam.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationJpaRepository extends JpaRepository<OrganizationEntity, Long> {

    boolean existsBySlug(String slug);
}
