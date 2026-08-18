package com.taskflow.modules.project.application.service;

import com.taskflow.common.exception.BusinessRuleViolationException;
import com.taskflow.common.exception.ResourceNotFoundException;
import com.taskflow.modules.project.application.dto.ProjectMemberResponse;
import com.taskflow.modules.project.application.dto.ProjectResponse;
import com.taskflow.modules.project.application.port.in.*;
import com.taskflow.modules.project.domain.Project;
import com.taskflow.modules.project.domain.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class ProjectApplicationService
        implements CreateProjectUseCase, GetProjectUseCase, AddProjectMemberUseCase {

    private final ProjectRepository projectRepository;

    public ProjectApplicationService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public ProjectResponse createProject(CreateProjectCommand command) {
        if (projectRepository.existsByKeyPrefix(command.workspaceId(), command.keyPrefix())) {
            throw new BusinessRuleViolationException(
                    "Project key prefix '" + command.keyPrefix().toUpperCase() + "' is already in use in this workspace.");
        }

        Project project = Project.create(
                command.workspaceId(),
                command.keyPrefix(),
                command.name(),
                command.description(),
                command.leadId()
        );

        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
        return mapToResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsByWorkspaceId(Long workspaceId) {
        return projectRepository.findByWorkspaceId(workspaceId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProjectResponse addMember(AddProjectMemberCommand command) {
        Project project = projectRepository.findById(command.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + command.projectId()));

        if (projectRepository.isUserMemberOfProject(command.projectId(), command.userId())) {
            throw new BusinessRuleViolationException("User is already a member of this project.");
        }

        projectRepository.addMember(command.projectId(), command.userId(), command.projectRole());
        Project updated = projectRepository.findById(command.projectId()).orElseThrow();
        return mapToResponse(updated);
    }

    private ProjectResponse mapToResponse(Project p) {
        List<ProjectMemberResponse> members = p.getMembers() != null ?
                p.getMembers().stream()
                        .map(m -> new ProjectMemberResponse(m.getId(), p.getId(), m.getUserId(), m.getProjectRole()))
                        .toList() : Collections.emptyList();

        return new ProjectResponse(
                p.getId(),
                p.getProjectCode(),
                p.getWorkspaceId(),
                p.getKeyPrefix(),
                p.getName(),
                p.getDescription(),
                p.getStatus(),
                p.getLeadId(),
                p.getNextTaskSeq(),
                members
        );
    }
}
