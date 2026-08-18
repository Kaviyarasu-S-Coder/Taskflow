package com.taskflow.modules.workspace.application.port.in;

import com.taskflow.modules.workspace.application.dto.WorkspaceResponse;

public interface CreateWorkspaceUseCase {
    WorkspaceResponse createWorkspace(CreateWorkspaceCommand command);
}
