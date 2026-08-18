package com.taskflow.modules.task.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TaskResponse(
        Long id,
        String taskCode,
        Long projectId,
        Long taskNumber,
        String formattedTaskKey,
        String title,
        String description,
        String status,
        String priority,
        String taskType,
        Integer storyPoints,
        BigDecimal estimatedHours,
        BigDecimal loggedHours,
        LocalDateTime dueDate,
        Long reporterId,
        Long assigneeId,
        Long parentTaskId,
        List<TaskDependencyResponse> dependencies
) {}
