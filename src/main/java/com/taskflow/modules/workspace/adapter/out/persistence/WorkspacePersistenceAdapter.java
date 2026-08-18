package com.taskflow.modules.workspace.adapter.out.persistence;

import com.taskflow.modules.workspace.domain.Workspace;
import com.taskflow.modules.workspace.domain.WorkspaceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class WorkspacePersistenceAdapter implements WorkspaceRepository {

    private final WorkspaceJpaRepository jpaRepository;

    public WorkspacePersistenceAdapter(WorkspaceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Workspace save(Workspace workspace) {
        WorkspaceEntity entity = toEntity(workspace);
        WorkspaceEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Workspace> findById(Long id) {
        return jpaRepository.findByIdAndDeletedFalse(id).map(this::toDomain);
    }

    @Override
    public List<Workspace> findByOrganizationId(Long organizationId) {
        return jpaRepository.findByOrganizationIdAndDeletedFalse(organizationId).stream()
                .map(this::toDomain)
                .toList();
    }

    private WorkspaceEntity toEntity(Workspace domain) {
        WorkspaceEntity entity = new WorkspaceEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setWorkspaceCode(domain.getWorkspaceCode());
        entity.setOrganizationId(domain.getOrganizationId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        return entity;
    }

    private Workspace toDomain(WorkspaceEntity entity) {
        Workspace w = new Workspace();
        w.setId(entity.getId());
        w.setWorkspaceCode(entity.getWorkspaceCode());
        w.setOrganizationId(entity.getOrganizationId());
        w.setName(entity.getName());
        w.setDescription(entity.getDescription());
        return w;
    }
}
