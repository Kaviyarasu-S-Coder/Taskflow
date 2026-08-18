package com.taskflow.modules.iam.application.dto;

import java.util.List;

/**
 * Response DTO returned after registration or login.
 * Contains both access and refresh tokens plus basic user info.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        Long userId,
        String email,
        String firstName,
        String lastName,
        List<String> roles
) {
    public static AuthResponse of(String accessToken, String refreshToken, long expiresInMs,
                                   Long userId, String email, String firstName, String lastName,
                                   List<String> roles) {
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                expiresInMs / 1000,
                userId,
                email,
                firstName,
                lastName,
                roles
        );
    }
}
