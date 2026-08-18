package com.taskflow.modules.collaboration.application.dto;

public record CommentResponse(
        Long id,
        String commentCode,
        Long taskId,
        Long authorId,
        String content
) {}
