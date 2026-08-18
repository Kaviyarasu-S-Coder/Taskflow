package com.taskflow.modules.workspace.application.dto;

public record WorkspaceResponse(
        Long id,
        String workspaceCode,
        Long organizationId,
        String name,
        String description
) {}
