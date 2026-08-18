package com.taskflow.modules.project.application.port.in;

public record AddProjectMemberCommand(
        Long projectId,
        Long userId,
        String projectRole
) {}
