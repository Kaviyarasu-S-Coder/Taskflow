package com.taskflow.modules.workspace.application.port.in;

import com.taskflow.modules.workspace.application.dto.WorkspaceResponse;

import java.util.List;

public interface GetWorkspaceUseCase {
    WorkspaceResponse getWorkspaceById(Long id);
    List<WorkspaceResponse> getWorkspacesByOrganizationId(Long organizationId);
}
