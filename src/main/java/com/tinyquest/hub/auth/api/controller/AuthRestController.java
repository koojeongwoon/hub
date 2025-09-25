package com.tinyquest.hub.auth.api.controller;

import com.tinyquest.hub.auth.api.dto.request.LoginRequest;
import com.tinyquest.hub.auth.api.dto.request.LogoutAllRequest;
import com.tinyquest.hub.auth.api.dto.request.LogoutRequest;
import com.tinyquest.hub.auth.api.dto.request.RefreshRequest;
import com.tinyquest.hub.auth.api.dto.request.RevokeAccessRequest;
import com.tinyquest.hub.auth.api.dto.response.TokenResponse;
import com.tinyquest.hub.auth.api.document.AuthRestControllerDoc;
import com.tinyquest.hub.auth.security.AuthPrincipal;
import com.tinyquest.hub.auth.service.AuthService;
import com.tinyquest.hub.auth.service.TokenRotationService;
import com.tinyquest.hub.shared.constants.ErrorCode;
import com.tinyquest.hub.shared.error.BusinessException;
import com.tinyquest.hub.shared.response.Response;
import com.tinyquest.hub.shared.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthRestController implements AuthRestControllerDoc {

    private final AuthService authService;
    private final TokenRotationService tokenRotationService;

    @PostMapping("/login")
    @Override
    public TokenResponse login(
            @Valid @RequestBody LoginRequest req,
            HttpServletRequest request
    ) {
        String clientIp = IpUtils.extractClientIp(request);
        return authService.login(req, clientIp);
    }

    @PostMapping("/refresh")
    @Override
    public TokenResponse refresh(
            @Valid @RequestBody RefreshRequest request,
            HttpServletRequest httpRequest
    ) {
        String clientIp = IpUtils.extractClientIp(httpRequest);
        return tokenRotationService.rotate(request.refreshToken(), request.deviceFingerprint(), request.scope(), clientIp);
    }

    @PostMapping("/logout")
    @Override
    public Response<Void> logout(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody LogoutRequest request
    ) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.USER_AUTH_2001);
        }
        authService.logout(principal.id(), request.sessionId(), request.reason());
        return Response.Success.of();
    }

    @PostMapping("/logout/all")
    @Override
    public Response<Void> logoutAll(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody(required = false) LogoutAllRequest request
    ) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.USER_AUTH_2001);
        }
        String reason = Objects.nonNull(request) ? request.reason() : null;
        authService.logoutAll(principal.id(), reason);
        return Response.Success.of();
    }

    @PostMapping("/revoke")
    @Override
    public Response<Void> revokeAccessToken(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody RevokeAccessRequest request
    ) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.USER_AUTH_2001);
        }
        authService.revokeAccessToken(principal.id(), request.jti(), request.reason());
        return Response.Success.of();
    }

}
