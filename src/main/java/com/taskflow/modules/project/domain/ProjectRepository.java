package com.taskflow.modules.project.domain;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(Long id);

    List<Project> findByWorkspaceId(Long workspaceId);

    boolean existsByKeyPrefix(Long workspaceId, String keyPrefix);

    boolean isUserMemberOfProject(Long projectId, Long userId);

    void addMember(Long projectId, Long userId, String role);
}
