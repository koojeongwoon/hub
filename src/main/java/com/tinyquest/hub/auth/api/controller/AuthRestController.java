package com.tinyquest.hub.auth.api.controller;

import com.tinyquest.hub.auth.api.dto.request.LoginRequest;
import com.tinyquest.hub.auth.api.dto.response.TokenResponse;
import com.tinyquest.hub.auth.service.AuthService;
import com.tinyquest.hub.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/login")
    public TokenResponse login(
            @Valid @RequestBody LoginRequest req
    ) {
        return authService.login(req);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // For JWT-based authentication, logout is typically handled on the client-side by deleting the token.
        // This endpoint can be used for any server-side cleanup if necessary (e.g., invalidating a refresh token).
        return ApiResponse.Success.of();
    }
}
