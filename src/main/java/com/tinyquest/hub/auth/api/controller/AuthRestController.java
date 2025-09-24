package com.tinyquest.hub.auth.api.controller;

import com.tinyquest.hub.auth.api.dto.request.LoginRequest;
import com.tinyquest.hub.auth.api.dto.request.LogoutAllRequest;
import com.tinyquest.hub.auth.api.dto.request.LogoutRequest;
import com.tinyquest.hub.auth.api.dto.request.RefreshRequest;
import com.tinyquest.hub.auth.api.dto.request.RevokeAccessRequest;
import com.tinyquest.hub.auth.api.dto.response.TokenResponse;
import com.tinyquest.hub.auth.security.AuthPrincipal;
import com.tinyquest.hub.auth.service.AuthService;
import com.tinyquest.hub.auth.service.TokenRotationService;
import com.tinyquest.hub.shared.constants.ErrorCode;
import com.tinyquest.hub.shared.error.BusinessException;
import com.tinyquest.hub.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
    public class AuthRestController {

    private final AuthService authService;
    private final TokenRotationService tokenRotationService;

    @PostMapping("/login")
    public TokenResponse login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest request
    ) {
        String clientIp = resolveClientIp(request);
        return authService.login(req, clientIp);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(
            @Valid @RequestBody RefreshRequest request,
            HttpServletRequest httpRequest
    ) {
        String clientIp = resolveClientIp(httpRequest);
        return tokenRotationService.rotate(request.refreshToken(), request.deviceFingerprint(), request.scope(), clientIp);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody LogoutRequest request
    ) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.USER_AUTH_2001);
        }
        authService.logout(principal.id(), request.sessionId(), request.reason());
        return ApiResponse.Success.of();
    }

    @PostMapping("/logout/all")
    public ApiResponse<Void> logoutAll(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody(required = false) LogoutAllRequest request
    ) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.USER_AUTH_2001);
        }
        String reason = request != null ? request.reason() : null;
        authService.logoutAll(principal.id(), reason);
        return ApiResponse.Success.of();
    }

    @PostMapping("/revoke")
    public ApiResponse<Void> revokeAccessToken(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody RevokeAccessRequest request
    ) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.USER_AUTH_2001);
        }
        authService.revokeAccessToken(principal.id(), request.jti(), request.reason());
        return ApiResponse.Success.of();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp;
        }
        return request.getRemoteAddr();
    }
}
