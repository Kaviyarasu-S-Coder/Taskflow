package com.taskflow.modules.project.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProjectRequest(

        @NotNull(message = "Workspace ID is required")
        Long workspaceId,

        @NotBlank(message = "Key prefix is required")
        @Size(min = 2, max = 10, message = "Key prefix must be between 2 and 10 characters")
        @Pattern(regexp = "^[A-Z0-9]+$", message = "Key prefix must be alphanumeric uppercase")
        String keyPrefix,

        @NotBlank(message = "Project name is required")
        @Size(max = 100, message = "Project name must not exceed 100 characters")
        String name,

        String description,

        @NotNull(message = "Lead User ID is required")
        Long leadId
) {}
