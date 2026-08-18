package com.taskflow.modules.collaboration.adapter.in.web;

import com.taskflow.modules.collaboration.application.dto.CommentResponse;
import com.taskflow.modules.collaboration.application.port.in.AddCommentCommand;
import com.taskflow.modules.collaboration.application.port.in.AddCommentUseCase;
import com.taskflow.modules.collaboration.application.port.in.GetCommentsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "Comments", description = "Endpoints for task comments")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final AddCommentUseCase addCommentUseCase;
    private final GetCommentsUseCase getCommentsUseCase;

    public CommentController(AddCommentUseCase addCommentUseCase,
                             GetCommentsUseCase getCommentsUseCase) {
        this.addCommentUseCase = addCommentUseCase;
        this.getCommentsUseCase = getCommentsUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a comment", description = "Adds a comment to a task")
    public ResponseEntity<CommentResponse> addComment(@Valid @RequestBody CommentRequest request) {
        AddCommentCommand command = new AddCommentCommand(
                request.taskId(),
                request.authorId(),
                request.content()
        );
        CommentResponse response = addCommentUseCase.addComment(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get comments by Task ID", description = "Lists all comments on a task in chronological order")
    public ResponseEntity<List<CommentResponse>> getCommentsByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(getCommentsUseCase.getCommentsByTaskId(taskId));
    }
}
