package com.taskflow.modules.workspace.adapter.in.web;

import com.taskflow.modules.workspace.application.dto.WorkspaceResponse;
import com.taskflow.modules.workspace.application.port.in.CreateWorkspaceCommand;
import com.taskflow.modules.workspace.application.port.in.CreateWorkspaceUseCase;
import com.taskflow.modules.workspace.application.port.in.GetWorkspaceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces")
@Tag(name = "Workspaces", description = "Endpoints for workspace management")
@SecurityRequirement(name = "bearerAuth")
public class WorkspaceController {

    private final CreateWorkspaceUseCase createWorkspaceUseCase;
    private final GetWorkspaceUseCase getWorkspaceUseCase;

    public WorkspaceController(CreateWorkspaceUseCase createWorkspaceUseCase,
                               GetWorkspaceUseCase getWorkspaceUseCase) {
        this.createWorkspaceUseCase = createWorkspaceUseCase;
        this.getWorkspaceUseCase = getWorkspaceUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a workspace", description = "Creates a new workspace within an organization")
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody WorkspaceRequest request) {
        CreateWorkspaceCommand command = new CreateWorkspaceCommand(
                request.organizationId(),
                request.name(),
                request.description()
        );
        WorkspaceResponse response = createWorkspaceUseCase.createWorkspace(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workspace by ID", description = "Returns details of a workspace")
    public ResponseEntity<WorkspaceResponse> getWorkspaceById(@PathVariable Long id) {
        return ResponseEntity.ok(getWorkspaceUseCase.getWorkspaceById(id));
    }

    @GetMapping("/org/{organizationId}")
    @Operation(summary = "Get workspaces by Organization ID", description = "Lists all workspaces belonging to an organization")
    public ResponseEntity<List<WorkspaceResponse>> getWorkspacesByOrganization(@PathVariable Long organizationId) {
        return ResponseEntity.ok(getWorkspaceUseCase.getWorkspacesByOrganizationId(organizationId));
    }
}
