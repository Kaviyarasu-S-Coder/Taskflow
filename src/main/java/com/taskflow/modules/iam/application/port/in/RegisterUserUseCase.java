package com.taskflow.modules.iam.application.port.in;

import com.taskflow.modules.iam.application.dto.AuthResponse;

/**
 * Inbound port — register a new user.
 */
public interface RegisterUserUseCase {

    AuthResponse register(RegisterUserCommand command);
}
