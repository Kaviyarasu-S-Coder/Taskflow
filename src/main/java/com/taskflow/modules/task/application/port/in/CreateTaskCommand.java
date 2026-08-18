package com.taskflow.modules.task.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTaskCommand(
        Long projectId,
        String title,
        String description,
        String priority,
        String taskType,
        Integer storyPoints,
        BigDecimal estimatedHours,
        LocalDateTime dueDate,
        Long reporterId,
        Long assigneeId,
        Long parentTaskId
) {}
