package com.taskflow.modules.task.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateTaskRequest(
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
