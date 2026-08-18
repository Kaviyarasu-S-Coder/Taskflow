package com.taskflow.modules.task.application.service;

import com.taskflow.common.exception.BusinessRuleViolationException;
import com.taskflow.common.exception.ResourceNotFoundException;
import com.taskflow.modules.project.adapter.out.persistence.ProjectEntity;
import com.taskflow.modules.project.adapter.out.persistence.ProjectJpaRepository;
import com.taskflow.modules.task.application.dto.TaskDependencyResponse;
import com.taskflow.modules.task.application.dto.TaskResponse;
import com.taskflow.modules.task.application.port.in.*;
import com.taskflow.modules.task.domain.Task;
import com.taskflow.modules.task.domain.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class TaskApplicationService
        implements CreateTaskUseCase, UpdateTaskUseCase, GetTaskUseCase, AddDependencyUseCase {

    private final TaskRepository taskRepository;
    private final ProjectJpaRepository projectJpaRepository;

    public TaskApplicationService(TaskRepository taskRepository,
                                  ProjectJpaRepository projectJpaRepository) {
        this.taskRepository = taskRepository;
        this.projectJpaRepository = projectJpaRepository;
    }

    @Override
    public TaskResponse createTask(CreateTaskCommand command) {
        ProjectEntity project = projectJpaRepository.findById(command.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + command.projectId()));

        Long taskNumber = project.getNextTaskSeq();
        project.setNextTaskSeq(taskNumber + 1);
        projectJpaRepository.save(project);

        Task task = Task.create(
                command.projectId(),
                taskNumber,
                command.title(),
                command.description(),
                command.priority(),
                command.taskType(),
                command.storyPoints(),
                command.estimatedHours(),
                command.dueDate(),
                command.reporterId(),
                command.assigneeId(),
                command.parentTaskId()
        );

        Task saved = taskRepository.save(task);
        return mapToResponse(saved, project.getKeyPrefix());
    }

    @Override
    public TaskResponse updateTask(UpdateTaskCommand command) {
        Task task = taskRepository.findById(command.taskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + command.taskId()));

        if (command.title() != null) task.setTitle(command.title());
        if (command.description() != null) task.setDescription(command.description());
        if (command.status() != null) task.setStatus(command.status());
        if (command.priority() != null) task.setPriority(command.priority());
        if (command.taskType() != null) task.setTaskType(command.taskType());
        if (command.storyPoints() != null) task.setStoryPoints(command.storyPoints());
        if (command.estimatedHours() != null) task.setEstimatedHours(command.estimatedHours());
        if (command.loggedHours() != null) task.setLoggedHours(command.loggedHours());
        if (command.dueDate() != null) task.setDueDate(command.dueDate());
        if (command.assigneeId() != null) task.setAssigneeId(command.assigneeId());

        Task updated = taskRepository.save(task);
        String keyPrefix = getProjectKeyPrefix(updated.getProjectId());
        return mapToResponse(updated, keyPrefix);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
        String keyPrefix = getProjectKeyPrefix(task.getProjectId());
        return mapToResponse(task, keyPrefix);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProjectId(Long projectId) {
        String keyPrefix = getProjectKeyPrefix(projectId);
        return taskRepository.findByProjectId(projectId).stream()
                .map(t -> mapToResponse(t, keyPrefix))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByAssigneeId(Long assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId).stream()
                .map(t -> {
                    String keyPrefix = getProjectKeyPrefix(t.getProjectId());
                    return mapToResponse(t, keyPrefix);
                })
                .toList();
    }

    @Override
    public TaskResponse addDependency(AddDependencyCommand command) {
        if (command.predecessorId().equals(command.successorId())) {
            throw new BusinessRuleViolationException("A task cannot depend on itself.");
        }

        if (taskRepository.existsDependency(command.predecessorId(), command.successorId())) {
            throw new BusinessRuleViolationException("Dependency relationship already exists.");
        }

        taskRepository.addDependency(command.predecessorId(), command.successorId(), command.dependencyType());
        Task updated = taskRepository.findById(command.predecessorId()).orElseThrow();
        String keyPrefix = getProjectKeyPrefix(updated.getProjectId());
        return mapToResponse(updated, keyPrefix);
    }

    private String getProjectKeyPrefix(Long projectId) {
        return projectJpaRepository.findById(projectId)
                .map(ProjectEntity::getKeyPrefix)
                .orElse("TASK");
    }

    private TaskResponse mapToResponse(Task t, String keyPrefix) {
        String formattedTaskKey = keyPrefix + "-" + t.getTaskNumber();

        List<TaskDependencyResponse> deps = t.getDependencies() != null ?
                t.getDependencies().stream()
                        .map(d -> new TaskDependencyResponse(d.getId(), d.getPredecessorId(), d.getSuccessorId(), d.getDependencyType()))
                        .toList() : Collections.emptyList();

        return new TaskResponse(
                t.getId(),
                t.getTaskCode(),
                t.getProjectId(),
                t.getTaskNumber(),
                formattedTaskKey,
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                t.getTaskType(),
                t.getStoryPoints(),
                t.getEstimatedHours(),
                t.getLoggedHours(),
                t.getDueDate(),
                t.getReporterId(),
                t.getAssigneeId(),
                t.getParentTaskId(),
                deps
        );
    }
}
