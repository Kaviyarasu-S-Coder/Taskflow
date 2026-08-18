package com.taskflow.modules.project.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long> {

    @Query("SELECT p FROM ProjectEntity p LEFT JOIN FETCH p.members WHERE p.id = :id AND p.deleted = false")
    Optional<ProjectEntity> findByIdWithMembers(@Param("id") Long id);

    List<ProjectEntity> findByWorkspaceIdAndDeletedFalse(Long workspaceId);

    boolean existsByWorkspaceIdAndKeyPrefixAndDeletedFalse(Long workspaceId, String keyPrefix);
}
