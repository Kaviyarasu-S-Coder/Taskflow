package com.taskflow.modules.task.adapter.in.web;

import com.taskflow.modules.task.application.dto.TaskResponse;
import com.taskflow.modules.task.application.port.in.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Endpoints for task and dependency management")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final GetTaskUseCase getTaskUseCase;
    private final AddDependencyUseCase addDependencyUseCase;

    public TaskController(CreateTaskUseCase createTaskUseCase,
                          UpdateTaskUseCase updateTaskUseCase,
                          GetTaskUseCase getTaskUseCase,
                          AddDependencyUseCase addDependencyUseCase) {
        this.createTaskUseCase = createTaskUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
        this.getTaskUseCase = getTaskUseCase;
        this.addDependencyUseCase = addDependencyUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a task", description = "Creates a new task within a project")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        CreateTaskCommand command = new CreateTaskCommand(
                request.projectId(),
                request.title(),
                request.description(),
                request.priority(),
                request.taskType(),
                request.storyPoints(),
                request.estimatedHours(),
                request.dueDate(),
                request.reporterId(),
                request.assigneeId(),
                request.parentTaskId()
        );
        TaskResponse response = createTaskUseCase.createTask(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Returns details of a task including formatted task key (e.g. PROJ-1)")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(getTaskUseCase.getTaskById(id));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get tasks by Project ID", description = "Lists all tasks belonging to a project")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(getTaskUseCase.getTasksByProjectId(projectId));
    }

    @GetMapping("/assignee/{assigneeId}")
    @Operation(summary = "Get tasks by Assignee ID", description = "Lists all tasks assigned to a specific user")
    public ResponseEntity<List<TaskResponse>> getTasksByAssignee(@PathVariable Long assigneeId) {
        return ResponseEntity.ok(getTaskUseCase.getTasksByAssigneeId(assigneeId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Updates details, status, or assignee of a task")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request) {
        UpdateTaskCommand command = new UpdateTaskCommand(
                id,
                request.title(),
                request.description(),
                request.status(),
                request.priority(),
                request.taskType(),
                request.storyPoints(),
                request.estimatedHours(),
                request.loggedHours(),
                request.dueDate(),
                request.assigneeId()
        );
        TaskResponse response = updateTaskUseCase.updateTask(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{successorId}/dependencies")
    @Operation(summary = "Add task dependency", description = "Makes this task depend on a predecessor task")
    public ResponseEntity<TaskResponse> addDependency(@PathVariable Long successorId, @Valid @RequestBody AddDependencyRequest request) {
        AddDependencyCommand command = new AddDependencyCommand(
                request.predecessorId(),
                successorId,
                request.dependencyType()
        );
        TaskResponse response = addDependencyUseCase.addDependency(command);
        return ResponseEntity.ok(response);
    }
}
