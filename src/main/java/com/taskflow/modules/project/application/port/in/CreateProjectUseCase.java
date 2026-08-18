package com.taskflow.modules.project.application.port.in;

import com.taskflow.modules.project.application.dto.ProjectResponse;

public interface CreateProjectUseCase {
    ProjectResponse createProject(CreateProjectCommand command);
}
