package com.taskflow.modules.workspace.application.port.in;

public record CreateWorkspaceCommand(
        Long organizationId,
        String name,
        String description
) {}
