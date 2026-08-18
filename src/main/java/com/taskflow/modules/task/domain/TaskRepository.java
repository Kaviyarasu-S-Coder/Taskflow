package com.taskflow.modules.task.domain;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(Long id);

    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssigneeId(Long assigneeId);

    void addDependency(Long predecessorId, Long successorId, String dependencyType);

    boolean existsDependency(Long predecessorId, Long successorId);
}
