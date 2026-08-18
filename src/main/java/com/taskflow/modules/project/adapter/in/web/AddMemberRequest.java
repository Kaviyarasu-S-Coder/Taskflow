package com.taskflow.modules.project.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddMemberRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Project role is required")
        String projectRole
) {}
