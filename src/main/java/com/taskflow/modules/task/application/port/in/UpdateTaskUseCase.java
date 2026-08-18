package com.taskflow.modules.task.application.port.in;

import com.taskflow.modules.task.application.dto.TaskResponse;

public interface UpdateTaskUseCase {
    TaskResponse updateTask(UpdateTaskCommand command);
}
