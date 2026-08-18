package com.taskflow.modules.iam.application.port.in;

/**
 * Inbound command for user authentication (login).
 */
public record AuthenticateCommand(
        String email,
        String password
) {}
