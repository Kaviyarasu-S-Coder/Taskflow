package com.taskflow.modules.project.adapter.in.web;

import com.taskflow.modules.project.application.dto.ProjectResponse;
import com.taskflow.modules.project.application.port.in.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Endpoints for project management")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final GetProjectUseCase getProjectUseCase;
    private final AddProjectMemberUseCase addProjectMemberUseCase;

    public ProjectController(CreateProjectUseCase createProjectUseCase,
                             GetProjectUseCase getProjectUseCase,
                             AddProjectMemberUseCase addProjectMemberUseCase) {
        this.createProjectUseCase = createProjectUseCase;
        this.getProjectUseCase = getProjectUseCase;
        this.addProjectMemberUseCase = addProjectMemberUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a project", description = "Creates a new project within a workspace")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        CreateProjectCommand command = new CreateProjectCommand(
                request.workspaceId(),
                request.keyPrefix(),
                request.name(),
                request.description(),
                request.leadId()
        );
        ProjectResponse response = createProjectUseCase.createProject(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Returns details of a project including its members")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(getProjectUseCase.getProjectById(id));
    }

    @GetMapping("/workspace/{workspaceId}")
    @Operation(summary = "Get projects by Workspace ID", description = "Lists all projects belonging to a workspace")
    public ResponseEntity<List<ProjectResponse>> getProjectsByWorkspace(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(getProjectUseCase.getProjectsByWorkspaceId(workspaceId));
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Add member to project", description = "Adds a team member to a project")
    public ResponseEntity<ProjectResponse> addMember(@PathVariable Long id, @Valid @RequestBody AddMemberRequest request) {
        AddProjectMemberCommand command = new AddProjectMemberCommand(
                id,
                request.userId(),
                request.projectRole()
        );
        ProjectResponse response = addProjectMemberUseCase.addMember(command);
        return ResponseEntity.ok(response);
    }
}
