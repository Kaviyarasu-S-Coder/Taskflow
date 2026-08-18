package com.taskflow.modules.collaboration.application.port.in;

import com.taskflow.modules.collaboration.application.dto.CommentResponse;

public interface AddCommentUseCase {
    CommentResponse addComment(AddCommentCommand command);
}
