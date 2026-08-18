package com.taskflow.modules.iam.application.port.in;

import com.taskflow.modules.iam.application.dto.AuthResponse;

/**
 * Inbound port — authenticate a user (login).
 */
public interface AuthenticateUserUseCase {

    AuthResponse authenticate(AuthenticateCommand command);
}
