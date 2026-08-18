package com.taskflow.modules.collaboration.domain;

import java.util.List;

public interface CommentRepository {

    Comment save(Comment comment);

    List<Comment> findByTaskId(Long taskId);
}
