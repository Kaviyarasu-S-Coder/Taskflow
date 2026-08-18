package com.taskflow.modules.project.application.dto;

import java.util.List;

public record ProjectResponse(
        Long id,
        String projectCode,
        Long workspaceId,
        String keyPrefix,
        String name,
        String description,
        String status,
        Long leadId,
        Long nextTaskSeq,
        List<ProjectMemberResponse> members
) {}
