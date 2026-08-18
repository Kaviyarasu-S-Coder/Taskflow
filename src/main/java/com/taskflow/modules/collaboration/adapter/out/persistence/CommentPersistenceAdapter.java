package com.taskflow.modules.collaboration.adapter.out.persistence;

import com.taskflow.modules.collaboration.domain.Comment;
import com.taskflow.modules.collaboration.domain.CommentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentPersistenceAdapter implements CommentRepository {

    private final CommentJpaRepository jpaRepository;

    public CommentPersistenceAdapter(CommentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Comment save(Comment comment) {
        CommentEntity entity = toEntity(comment);
        CommentEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Comment> findByTaskId(Long taskId) {
        return jpaRepository.findByTaskIdAndDeletedFalseOrderByCreatedAtAsc(taskId).stream()
                .map(this::toDomain)
                .toList();
    }

    private CommentEntity toEntity(Comment domain) {
        CommentEntity entity = new CommentEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setCommentCode(domain.getCommentCode());
        entity.setTaskId(domain.getTaskId());
        entity.setAuthorId(domain.getAuthorId());
        entity.setContent(domain.getContent());
        return entity;
    }

    private Comment toDomain(CommentEntity entity) {
        Comment c = new Comment();
        c.setId(entity.getId());
        c.setCommentCode(entity.getCommentCode());
        c.setTaskId(entity.getTaskId());
        c.setAuthorId(entity.getAuthorId());
        c.setContent(entity.getContent());
        return c;
    }
}
