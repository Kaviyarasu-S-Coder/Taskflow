package com.taskflow.modules.task.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {

    @Query("SELECT t FROM TaskEntity t LEFT JOIN FETCH t.dependencies WHERE t.id = :id AND t.deleted = false")
    Optional<TaskEntity> findByIdWithDependencies(@Param("id") Long id);

    List<TaskEntity> findByProjectIdAndDeletedFalse(Long projectId);

    List<TaskEntity> findByAssigneeIdAndDeletedFalse(Long assigneeId);

    Optional<TaskEntity> findByProjectIdAndTaskNumberAndDeletedFalse(Long projectId, Long taskNumber);
}
