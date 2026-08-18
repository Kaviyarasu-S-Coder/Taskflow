package com.taskflow.modules.workspace.domain;

import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository {

    Workspace save(Workspace workspace);

    Optional<Workspace> findById(Long id);

    List<Workspace> findByOrganizationId(Long organizationId);
}
