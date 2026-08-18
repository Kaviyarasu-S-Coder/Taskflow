package com.taskflow.modules.project.application.dto;

public record ProjectMemberResponse(
        Long id,
        Long projectId,
        Long userId,
        String projectRole
) {}
