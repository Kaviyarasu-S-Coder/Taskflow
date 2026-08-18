package com.taskflow.modules.collaboration.application.port.in;

import com.taskflow.modules.collaboration.application.dto.CommentResponse;

import java.util.List;

public interface GetCommentsUseCase {
    List<CommentResponse> getCommentsByTaskId(Long taskId);
}
