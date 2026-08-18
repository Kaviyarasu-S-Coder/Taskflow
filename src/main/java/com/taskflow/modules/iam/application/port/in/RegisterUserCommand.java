package com.taskflow.modules.iam.application.port.in;

/**
 * Inbound command for user registration.
 * Immutable value object — validated by the web adapter before passing here.
 */
public record RegisterUserCommand(
        String firstName,
        String lastName,
        String email,
        String password,
        String organizationName
) {}
