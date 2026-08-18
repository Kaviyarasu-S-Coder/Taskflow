package com.taskflow.modules.project.application.port.in;

import com.taskflow.modules.project.application.dto.ProjectResponse;

import java.util.List;

public interface GetProjectUseCase {
    ProjectResponse getProjectById(Long id);
    List<ProjectResponse> getProjectsByWorkspaceId(Long workspaceId);
}
