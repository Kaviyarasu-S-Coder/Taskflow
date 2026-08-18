package com.taskflow.modules.collaboration.application.service;

import com.taskflow.modules.collaboration.application.dto.CommentResponse;
import com.taskflow.modules.collaboration.application.port.in.AddCommentCommand;
import com.taskflow.modules.collaboration.application.port.in.AddCommentUseCase;
import com.taskflow.modules.collaboration.application.port.in.GetCommentsUseCase;
import com.taskflow.modules.collaboration.domain.Comment;
import com.taskflow.modules.collaboration.domain.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentApplicationService implements AddCommentUseCase, GetCommentsUseCase {

    private final CommentRepository commentRepository;

    public CommentApplicationService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public CommentResponse addComment(AddCommentCommand command) {
        Comment comment = Comment.create(
                command.taskId(),
                command.authorId(),
                command.content()
        );

        Comment saved = commentRepository.save(comment);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTaskId(Long taskId) {
        return commentRepository.findByTaskId(taskId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private CommentResponse mapToResponse(Comment c) {
        return new CommentResponse(
                c.getId(),
                c.getCommentCode(),
                c.getTaskId(),
                c.getAuthorId(),
                c.getContent()
        );
    }
}
