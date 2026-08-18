package com.taskflow.modules.project.application.port.in;

public record CreateProjectCommand(
        Long workspaceId,
        String keyPrefix,
        String name,
        String description,
        Long leadId
) {}
