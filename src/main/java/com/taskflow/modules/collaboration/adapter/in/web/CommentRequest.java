package com.taskflow.modules.collaboration.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequest(

        @NotNull(message = "Task ID is required")
        Long taskId,

        @NotNull(message = "Author User ID is required")
        Long authorId,

        @NotBlank(message = "Comment content is required")
        String content
) {}
