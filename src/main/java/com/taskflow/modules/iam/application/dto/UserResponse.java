package com.taskflow.modules.iam.application.dto;

import java.util.List;

/**
 * Lightweight user profile response DTO.
 */
public record UserResponse(
        Long id,
        String userCode,
        String email,
        String firstName,
        String lastName,
        String status,
        List<String> roles
) {}
