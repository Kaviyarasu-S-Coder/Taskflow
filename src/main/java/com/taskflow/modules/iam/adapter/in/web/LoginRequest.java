package com.taskflow.modules.iam.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * HTTP request body for user login.
 */
public record LoginRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {}
