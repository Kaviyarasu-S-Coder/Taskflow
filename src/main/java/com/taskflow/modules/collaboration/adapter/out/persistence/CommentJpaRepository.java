package com.taskflow.modules.collaboration.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByTaskIdAndDeletedFalseOrderByCreatedAtAsc(Long taskId);
}
