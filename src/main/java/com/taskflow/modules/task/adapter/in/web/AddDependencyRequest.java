package com.taskflow.modules.task.adapter.in.web;

import jakarta.validation.constraints.NotNull;

public record AddDependencyRequest(

        @NotNull(message = "Predecessor Task ID is required")
        Long predecessorId,

        String dependencyType
) {}
