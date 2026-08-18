package com.taskflow.modules.task.application.port.in;

import com.taskflow.modules.task.application.dto.TaskResponse;

import java.util.List;

public interface GetTaskUseCase {
    TaskResponse getTaskById(Long id);
    List<TaskResponse> getTasksByProjectId(Long projectId);
    List<TaskResponse> getTasksByAssigneeId(Long assigneeId);
}
