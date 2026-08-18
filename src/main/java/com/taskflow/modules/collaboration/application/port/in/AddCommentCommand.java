package com.taskflow.modules.collaboration.application.port.in;

public record AddCommentCommand(
        Long taskId,
        Long authorId,
        String content
) {}
