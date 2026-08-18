package com.taskflow.modules.collaboration.adapter.out.persistence;

import com.taskflow.common.domain.BaseAuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class CommentEntity extends BaseAuditEntity {

    @Column(name = "comment_code", nullable = false, unique = true, length = 30)
    private String commentCode;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public String getCommentCode() { return commentCode; }
    public void setCommentCode(String commentCode) { this.commentCode = commentCode; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
