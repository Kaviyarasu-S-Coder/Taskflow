package com.taskflow.modules.task.adapter.out.persistence;

import com.taskflow.modules.task.domain.Task;
import com.taskflow.modules.task.domain.TaskDependency;
import com.taskflow.modules.task.domain.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TaskPersistenceAdapter implements TaskRepository {

    private final TaskJpaRepository taskJpaRepository;
    private final TaskDependencyJpaRepository dependencyJpaRepository;

    public TaskPersistenceAdapter(TaskJpaRepository taskJpaRepository,
                                  TaskDependencyJpaRepository dependencyJpaRepository) {
        this.taskJpaRepository = taskJpaRepository;
        this.dependencyJpaRepository = dependencyJpaRepository;
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = toEntity(task);
        TaskEntity saved = taskJpaRepository.save(entity);
        return findById(saved.getId()).orElseThrow();
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskJpaRepository.findByIdWithDependencies(id).map(this::toDomain);
    }

    @Override
    public List<Task> findByProjectId(Long projectId) {
        return taskJpaRepository.findByProjectIdAndDeletedFalse(projectId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Task> findByAssigneeId(Long assigneeId) {
        return taskJpaRepository.findByAssigneeIdAndDeletedFalse(assigneeId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void addDependency(Long predecessorId, Long successorId, String dependencyType) {
        TaskEntity predecessor = taskJpaRepository.findById(predecessorId).orElseThrow();
        TaskEntity successor = taskJpaRepository.findById(successorId).orElseThrow();
        TaskDependencyEntity entity = new TaskDependencyEntity(predecessor, successor, dependencyType);
        TaskDependencyEntity saved = dependencyJpaRepository.save(entity);
        predecessor.getDependencies().add(saved);
    }

    @Override
    public boolean existsDependency(Long predecessorId, Long successorId) {
        return dependencyJpaRepository.existsByPredecessorIdAndSuccessorIdAndDeletedFalse(predecessorId, successorId);
    }

    private TaskEntity toEntity(Task domain) {
        TaskEntity entity = new TaskEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setTaskCode(domain.getTaskCode());
        entity.setProjectId(domain.getProjectId());
        entity.setTaskNumber(domain.getTaskNumber());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setPriority(domain.getPriority());
        entity.setTaskType(domain.getTaskType());
        entity.setStoryPoints(domain.getStoryPoints());
        entity.setEstimatedHours(domain.getEstimatedHours());
        entity.setLoggedHours(domain.getLoggedHours());
        entity.setDueDate(domain.getDueDate());
        entity.setReporterId(domain.getReporterId());
        entity.setAssigneeId(domain.getAssigneeId());
        entity.setParentTaskId(domain.getParentTaskId());
        return entity;
    }

    private Task toDomain(TaskEntity entity) {
        Task t = new Task();
        t.setId(entity.getId());
        t.setTaskCode(entity.getTaskCode());
        t.setProjectId(entity.getProjectId());
        t.setTaskNumber(entity.getTaskNumber());
        t.setTitle(entity.getTitle());
        t.setDescription(entity.getDescription());
        t.setStatus(entity.getStatus());
        t.setPriority(entity.getPriority());
        t.setTaskType(entity.getTaskType());
        t.setStoryPoints(entity.getStoryPoints());
        t.setEstimatedHours(entity.getEstimatedHours());
        t.setLoggedHours(entity.getLoggedHours());
        t.setDueDate(entity.getDueDate());
        t.setReporterId(entity.getReporterId());
        t.setAssigneeId(entity.getAssigneeId());
        t.setParentTaskId(entity.getParentTaskId());

        if (entity.getDependencies() != null) {
            t.setDependencies(entity.getDependencies().stream()
                    .map(de -> {
                        TaskDependency td = new TaskDependency();
                        td.setId(de.getId());
                        td.setPredecessorId(de.getPredecessor().getId());
                        td.setSuccessorId(de.getSuccessor().getId());
                        td.setDependencyType(de.getDependencyType());
                        return td;
                    }).toList());
        }
        return t;
    }
}
