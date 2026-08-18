package com.taskflow.modules.iam.adapter.in.web;

import com.taskflow.modules.iam.application.dto.AuthResponse;
import com.taskflow.modules.iam.application.port.in.AuthenticateCommand;
import com.taskflow.modules.iam.application.port.in.AuthenticateUserUseCase;
import com.taskflow.modules.iam.application.port.in.RefreshTokenUseCase;
import com.taskflow.modules.iam.application.port.in.RegisterUserCommand;
import com.taskflow.modules.iam.application.port.in.RegisterUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for IAM authentication endpoints.
 * All endpoints under /api/v1/auth/** are public (see SecurityConfig).
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "IAM endpoints for user registration, login, and token management")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          AuthenticateUserUseCase authenticateUserUseCase,
                          RefreshTokenUseCase refreshTokenUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
    }

    // ── POST /api/v1/auth/register ─────────────────────────────────────────────

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user",
               description = "Creates a new user account, auto-creates an organization if needed, " +
                             "assigns ROLE_DEVELOPER, and returns a token pair.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error or duplicate email",
                     content = @Content(schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password(),
                request.organizationName()
        );
        AuthResponse response = registerUserUseCase.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── POST /api/v1/auth/login ────────────────────────────────────────────────

    @PostMapping("/login")
    @Operation(summary = "Authenticate user (login)",
               description = "Validates credentials and returns a JWT access + refresh token pair.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                     content = @Content(schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticateCommand command = new AuthenticateCommand(request.email(), request.password());
        AuthResponse response = authenticateUserUseCase.authenticate(command);
        return ResponseEntity.ok(response);
    }

    // ── POST /api/v1/auth/refresh ──────────────────────────────────────────────

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token",
               description = "Exchanges a valid refresh token for a new access + refresh token pair.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @ApiResponse(responseCode = "400", description = "Refresh token is invalid or expired",
                     content = @Content(schema = @Schema(implementation = Object.class)))
    })
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = refreshTokenUseCase.refresh(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    // ── POST /api/v1/auth/logout ───────────────────────────────────────────────

    @PostMapping("/logout")
    @Operation(summary = "Logout (client-side token invalidation)",
               description = "Stateless logout — the client should discard the token. " +
                             "Server-side token revocation is handled by token expiry.")
    @ApiResponse(responseCode = "200", description = "Logout successful")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout successful. Please discard your tokens."));
    }
}
