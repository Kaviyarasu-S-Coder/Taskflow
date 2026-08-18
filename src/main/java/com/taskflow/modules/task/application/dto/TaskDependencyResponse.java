package com.taskflow.modules.task.application.dto;

public record TaskDependencyResponse(
        Long id,
        Long predecessorId,
        Long successorId,
        String dependencyType
) {}
