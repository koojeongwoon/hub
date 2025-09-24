package com.tinyquest.hub.auth.service;

import com.tinyquest.hub.auth.api.dto.request.LoginRequest;
import com.tinyquest.hub.auth.api.dto.response.TokenResponse;
import com.tinyquest.hub.auth.constants.TokenEventType;
import com.tinyquest.hub.auth.domain.entity.AccessTokenJti;
import com.tinyquest.hub.auth.domain.entity.AuthSession;
import com.tinyquest.hub.auth.domain.entity.RefreshToken;
import com.tinyquest.hub.auth.domain.repository.AccessTokenJtiRepository;
import com.tinyquest.hub.auth.domain.repository.AuthSessionRepository;
import com.tinyquest.hub.auth.domain.repository.RefreshTokenRepository;
import com.tinyquest.hub.auth.infra.jwt.JwtIssuer;
import com.tinyquest.hub.auth.support.AuthTokenUtils;
import com.tinyquest.hub.shared.constants.ErrorCode;
import com.tinyquest.hub.shared.error.BusinessException;
import com.tinyquest.hub.shared.port.user.UserDetailsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDetailsPort userDetailsPort;
    private final JwtIssuer jwtIssuer;
    private final PasswordEncoder passwordEncoder;
    private final AuthSessionRepository authSessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenJtiRepository accessTokenJtiRepository;
    private final TokenAuditService tokenAuditService;
    private final TokenRevocationService tokenRevocationService;
    private final Clock clock;

    @Transactional
    public TokenResponse login(LoginRequest req, String clientIp) {
        var user = userDetailsPort.findByEmail(req.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND_4001));

        if (!user.enabled() || !passwordEncoder.matches(req.password(), user.passwordHash())) {
            throw new BusinessException(ErrorCode.USER_VALIDATION_1001);
        }

        String clientId = StringUtils.hasText(req.clientId()) ? req.clientId() : "default";
        String deviceName = StringUtils.hasText(req.deviceName()) ? req.deviceName() : "unknown";
        byte[] deviceFingerprint = AuthTokenUtils.decodeFingerprint(req.deviceFingerprint());
        String scope = StringUtils.hasText(req.scope()) ? req.scope() : null;

        Instant issuedAt = clock.instant();
        AuthSession session = AuthSession.create(user.id(), clientId, deviceName, deviceFingerprint, clientIp, issuedAt);
        session = authSessionRepository.save(session);

        var accessToken = jwtIssuer.issueAccessToken(user.id(), user.username(), session.getId());
        var refreshToken = jwtIssuer.issueRefreshToken(user.id(), user.username(), session.getId(), 0, scope);

        AccessTokenJti accessEntity = AccessTokenJti.issue(accessToken.jti(), user.id(), session.getId(), accessToken.issuedAt(), accessToken.expiresAt());
        accessTokenJtiRepository.save(accessEntity);

        RefreshToken refreshEntity = RefreshToken.issue(session, refreshToken.tokenHash(), refreshToken.rotationIndex(), refreshToken.scope(), refreshToken.issuedAt(), refreshToken.expiresAt());
        session.addRefreshToken(refreshEntity);
        refreshTokenRepository.save(refreshEntity);

        var issueDetail = new java.util.HashMap<String, Object>();
        issueDetail.put("clientId", clientId);
        issueDetail.put("deviceName", deviceName);
        issueDetail.put("kid", accessToken.kid());
        issueDetail.put("ip", clientIp);
        if (scope != null) {
            issueDetail.put("scope", scope);
        }
        tokenAuditService.record(user.id(), session.getId(), refreshEntity.getId(), TokenEventType.ISSUE, issueDetail);

        return new TokenResponse(
                AuthTokenUtils.TOKEN_TYPE_BEARER,
                accessToken.token(),
                accessToken.expiresAt(),
                refreshToken.token(),
                refreshToken.expiresAt(),
                session.getId()
        );
    }

    @Transactional
    public void logout(Long userId, UUID sessionId, String reason) {
        if (sessionId == null) {
            throw new BusinessException(ErrorCode.AUTH_SESSION_NOT_FOUND_4002);
        }
        tokenRevocationService.revokeSessionWithTokens(userId, sessionId, reason, false);
    }

    @Transactional
    public void logoutAll(Long userId, String reason) {
        tokenRevocationService.revokeAllActiveSessions(userId, reason);
    }

    @Transactional
    public void revokeAccessToken(Long userId, UUID jti, String reason) {
        if (jti == null) {
            throw new BusinessException(ErrorCode.USER_AUTH_2001);
        }

        Instant now = clock.instant();
        String normalizedReason = StringUtils.hasText(reason) ? reason : TokenEventType.REVOKE.name();

        AccessTokenJti token = accessTokenJtiRepository.findByJtiAndUserId(jti, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_AUTH_2001));

        if (token.getRevokedAt() == null) {
            accessTokenJtiRepository.revokeByJti(jti, userId, now, normalizedReason);
            var detail = new java.util.HashMap<String, Object>();
            detail.put("reason", normalizedReason);
            detail.put("jti", jti);
            tokenAuditService.record(userId, token.getSessionId(), null, TokenEventType.REVOKE, detail);
        }
    }

}
