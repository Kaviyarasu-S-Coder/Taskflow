package com.taskflow.modules.workspace.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceJpaRepository extends JpaRepository<WorkspaceEntity, Long> {

    List<WorkspaceEntity> findByOrganizationIdAndDeletedFalse(Long organizationId);

    Optional<WorkspaceEntity> findByIdAndDeletedFalse(Long id);

    Optional<WorkspaceEntity> findByWorkspaceCodeAndDeletedFalse(String workspaceCode);
}
