package com.taskflow.modules.collaboration.domain;

import java.util.UUID;

public class Comment {

    private Long id;
    private String commentCode;
    private Long taskId;
    private Long authorId;
    private String content;

    public static Comment create(Long taskId, Long authorId, String content) {
        Comment c = new Comment();
        c.commentCode = "CMT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        c.taskId = taskId;
        c.authorId = authorId;
        c.content = content;
        return c;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCommentCode() { return commentCode; }
    public void setCommentCode(String commentCode) { this.commentCode = commentCode; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
