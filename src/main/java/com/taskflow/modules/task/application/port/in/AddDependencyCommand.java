package com.taskflow.modules.task.application.port.in;

public record AddDependencyCommand(
        Long predecessorId,
        Long successorId,
        String dependencyType
) {}
