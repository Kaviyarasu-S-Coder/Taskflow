package com.taskflow.modules.task.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDependencyJpaRepository extends JpaRepository<TaskDependencyEntity, Long> {

    List<TaskDependencyEntity> findByPredecessorIdAndDeletedFalse(Long predecessorId);

    List<TaskDependencyEntity> findBySuccessorIdAndDeletedFalse(Long successorId);

    boolean existsByPredecessorIdAndSuccessorIdAndDeletedFalse(Long predecessorId, Long successorId);
}
