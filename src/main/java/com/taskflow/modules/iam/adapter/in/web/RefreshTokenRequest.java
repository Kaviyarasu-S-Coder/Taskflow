package com.taskflow.modules.iam.adapter.in.web;

import jakarta.validation.constraints.NotBlank;

/**
 * HTTP request body for token refresh.
 */
public record RefreshTokenRequest(

        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {}
