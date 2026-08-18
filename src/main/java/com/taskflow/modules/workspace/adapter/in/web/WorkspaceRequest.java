package com.taskflow.modules.workspace.adapter.in.web;

import jakarta.validation.constraints.NotNull;

public record WorkspaceRequest(

        @NotNull(message = "Organization ID is required")
        Long organizationId,

        @jakarta.validation.constraints.NotBlank(message = "Workspace name is required")
        @jakarta.validation.constraints.Size(max = 100, message = "Workspace name must not exceed 100 characters")
        String name,

        String description
) {}
