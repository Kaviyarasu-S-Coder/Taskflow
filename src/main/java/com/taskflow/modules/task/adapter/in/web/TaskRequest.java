package com.taskflow.modules.task.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TaskRequest(

        @NotNull(message = "Project ID is required")
        Long projectId,

        @NotBlank(message = "Task title is required")
        @Size(max = 200, message = "Task title must not exceed 200 characters")
        String title,

        String description,
        String priority,
        String taskType,
        Integer storyPoints,
        BigDecimal estimatedHours,
        LocalDateTime dueDate,

        @NotNull(message = "Reporter User ID is required")
        Long reporterId,

        Long assigneeId,
        Long parentTaskId
) {}
