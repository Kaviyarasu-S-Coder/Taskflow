package com.taskflow.modules.project.adapter.out.persistence;

import com.taskflow.modules.project.domain.Project;
import com.taskflow.modules.project.domain.ProjectMember;
import com.taskflow.modules.project.domain.ProjectRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProjectPersistenceAdapter implements ProjectRepository {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectMemberJpaRepository memberJpaRepository;

    public ProjectPersistenceAdapter(ProjectJpaRepository projectJpaRepository,
                                     ProjectMemberJpaRepository memberJpaRepository) {
        this.projectJpaRepository = projectJpaRepository;
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    public Project save(Project project) {
        ProjectEntity entity = toEntity(project);
        ProjectEntity saved = projectJpaRepository.save(entity);

        // Ensure members are persisted
        if (project.getMembers() != null && !project.getMembers().isEmpty()) {
            for (ProjectMember pm : project.getMembers()) {
                if (!memberJpaRepository.existsByProjectIdAndUserIdAndDeletedFalse(saved.getId(), pm.getUserId())) {
                    ProjectMemberEntity me = new ProjectMemberEntity(saved, pm.getUserId(), pm.getProjectRole());
                    ProjectMemberEntity savedMember = memberJpaRepository.save(me);
                    saved.getMembers().add(savedMember);
                }
            }
        }

        return toDomain(saved);
    }

    @Override
    public Optional<Project> findById(Long id) {
        return projectJpaRepository.findByIdWithMembers(id).map(this::toDomain);
    }

    @Override
    public List<Project> findByWorkspaceId(Long workspaceId) {
        return projectJpaRepository.findByWorkspaceIdAndDeletedFalse(workspaceId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByKeyPrefix(Long workspaceId, String keyPrefix) {
        return projectJpaRepository.existsByWorkspaceIdAndKeyPrefixAndDeletedFalse(workspaceId, keyPrefix.toUpperCase());
    }

    @Override
    public boolean isUserMemberOfProject(Long projectId, Long userId) {
        return memberJpaRepository.existsByProjectIdAndUserIdAndDeletedFalse(projectId, userId);
    }

    @Override
    public void addMember(Long projectId, Long userId, String role) {
        ProjectEntity projectEntity = projectJpaRepository.findById(projectId).orElseThrow();
        ProjectMemberEntity memberEntity = new ProjectMemberEntity(projectEntity, userId, role);
        ProjectMemberEntity savedMember = memberJpaRepository.save(memberEntity);
        projectEntity.getMembers().add(savedMember);
    }

    private ProjectEntity toEntity(Project domain) {
        ProjectEntity entity = new ProjectEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setProjectCode(domain.getProjectCode());
        entity.setWorkspaceId(domain.getWorkspaceId());
        entity.setKeyPrefix(domain.getKeyPrefix());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setStatus(domain.getStatus());
        entity.setLeadId(domain.getLeadId());
        entity.setNextTaskSeq(domain.getNextTaskSeq() != null ? domain.getNextTaskSeq() : 1L);
        return entity;
    }

    private Project toDomain(ProjectEntity entity) {
        Project p = new Project();
        p.setId(entity.getId());
        p.setProjectCode(entity.getProjectCode());
        p.setWorkspaceId(entity.getWorkspaceId());
        p.setKeyPrefix(entity.getKeyPrefix());
        p.setName(entity.getName());
        p.setDescription(entity.getDescription());
        p.setStatus(entity.getStatus());
        p.setLeadId(entity.getLeadId());
        p.setNextTaskSeq(entity.getNextTaskSeq());

        if (entity.getMembers() != null) {
            p.setMembers(entity.getMembers().stream()
                    .map(me -> {
                        ProjectMember pm = new ProjectMember();
                        pm.setId(me.getId());
                        pm.setProjectId(entity.getId());
                        pm.setUserId(me.getUserId());
                        pm.setProjectRole(me.getProjectRole());
                        return pm;
                    }).toList());
        }
        return p;
    }
}
