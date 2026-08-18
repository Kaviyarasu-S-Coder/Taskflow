package com.taskflow.modules.iam.application.port.in;

import com.taskflow.modules.iam.application.dto.AuthResponse;

/**
 * Inbound port — refresh an access token using a valid refresh token.
 */
public interface RefreshTokenUseCase {

    AuthResponse refresh(String refreshToken);
}
