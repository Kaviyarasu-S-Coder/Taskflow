package com.taskflow.modules.workspace.application.service;

import com.taskflow.common.exception.ResourceNotFoundException;
import com.taskflow.modules.workspace.application.dto.WorkspaceResponse;
import com.taskflow.modules.workspace.application.port.in.CreateWorkspaceCommand;
import com.taskflow.modules.workspace.application.port.in.CreateWorkspaceUseCase;
import com.taskflow.modules.workspace.application.port.in.GetWorkspaceUseCase;
import com.taskflow.modules.workspace.domain.Workspace;
import com.taskflow.modules.workspace.domain.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkspaceApplicationService implements CreateWorkspaceUseCase, GetWorkspaceUseCase {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceApplicationService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public WorkspaceResponse createWorkspace(CreateWorkspaceCommand command) {
        Workspace workspace = Workspace.create(
                command.organizationId(),
                command.name(),
                command.description()
        );
        Workspace saved = workspaceRepository.save(workspace);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkspaceResponse getWorkspaceById(Long id) {
        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found with ID: " + id));
        return mapToResponse(workspace);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getWorkspacesByOrganizationId(Long organizationId) {
        return workspaceRepository.findByOrganizationId(organizationId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private WorkspaceResponse mapToResponse(Workspace w) {
        return new WorkspaceResponse(
                w.getId(),
                w.getWorkspaceCode(),
                w.getOrganizationId(),
                w.getName(),
                w.getDescription()
        );
    }
}
