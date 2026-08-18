package com.taskflow.modules.task.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateTaskCommand(
        Long taskId,
        String title,
        String description,
        String status,
        String priority,
        String taskType,
        Integer storyPoints,
        BigDecimal estimatedHours,
        BigDecimal loggedHours,
        LocalDateTime dueDate,
        Long assigneeId
) {}
