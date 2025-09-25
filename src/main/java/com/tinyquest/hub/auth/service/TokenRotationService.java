package com.tinyquest.hub.auth.service;

import com.tinyquest.hub.auth.api.dto.response.TokenResponse;
import com.tinyquest.hub.auth.constants.TokenEventType;
import com.tinyquest.hub.auth.domain.entity.AccessTokenJti;
import com.tinyquest.hub.auth.domain.entity.AuthSession;
import com.tinyquest.hub.auth.domain.entity.RefreshToken;
import com.tinyquest.hub.auth.domain.repository.AccessTokenJtiRepository;
import com.tinyquest.hub.auth.domain.repository.AuthSessionRepository;
import com.tinyquest.hub.auth.domain.repository.RefreshTokenRepository;
import com.tinyquest.hub.auth.infra.jwt.JwtIssuer;
import com.tinyquest.hub.auth.infra.jwt.JwtVerifier;
import com.tinyquest.hub.auth.support.AuthTokenUtils;
import com.tinyquest.hub.shared.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;

import static com.tinyquest.hub.shared.constants.ErrorCode.AUTH_SESSION_NOT_FOUND_4002;
import static com.tinyquest.hub.shared.utils.Hashing.sha256;

@Service
@RequiredArgsConstructor
public class TokenRotationService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthSessionRepository authSessionRepository;
    private final AccessTokenJtiRepository accessTokenJtiRepository;
    private final JwtIssuer jwtIssuer;
    private final JwtVerifier jwtVerifier;
    private final TokenAuditService tokenAuditService;
    private final TokenRevocationService tokenRevocationService;
    private final Clock clock;

    @Transactional
    public TokenResponse rotate(String refreshTokenValue, String fingerprint, String scopeOverride, String clientIp) {
        var claims = jwtVerifier.verifyRefreshToken(refreshTokenValue);
        Instant now = clock.instant();

        byte[] hash = sha256(refreshTokenValue);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BusinessException(AUTH_SESSION_NOT_FOUND_4002));

        AuthSession session = refreshToken.getSession();

        if (session == null || session.getRevokedAt() != null) {
            throw new BusinessException(AUTH_SESSION_NOT_FOUND_4002);
        }

        if (!session.getUserId().equals(claims.userId()) || (claims.sessionId() != null && !session.getId().equals(claims.sessionId()))) {
            tokenRevocationService.handleReuseDetection(session, refreshToken, clientIp);
            throw new BusinessException(AUTH_SESSION_NOT_FOUND_4002);
        }

        if (refreshToken.getConsumedAt() != null || refreshToken.getRevokedAt() != null) {
            tokenRevocationService.handleReuseDetection(session, refreshToken, clientIp);
            throw new BusinessException(AUTH_SESSION_NOT_FOUND_4002);
        }

        if (refreshToken.getExpiresAt().isBefore(now)) {
            tokenRevocationService.handleReuseDetection(session, refreshToken, clientIp);
            throw new BusinessException(AUTH_SESSION_NOT_FOUND_4002);
        }

        if (refreshToken.getRotationIndex() != claims.rotation()) {
            tokenRevocationService.handleReuseDetection(session, refreshToken, clientIp);
            throw new BusinessException(AUTH_SESSION_NOT_FOUND_4002);
        }

        refreshTokenRepository.findTopBySession_IdOrderByRotationIndexDesc(session.getId())
                .ifPresent(latest -> {
                    if (!latest.getId().equals(refreshToken.getId())) {
                        tokenRevocationService.handleReuseDetection(session, refreshToken, clientIp);
                        throw new BusinessException(AUTH_SESSION_NOT_FOUND_4002);
                    }
                });

        session.touchLastSeen(now);
        byte[] presentedFingerprint = AuthTokenUtils.decodeFingerprint(fingerprint);
        if (presentedFingerprint != null) {
            refreshToken.setPresentedFingerprint(presentedFingerprint);
        }

        refreshToken.markConsumed(now);
        refreshTokenRepository.save(refreshToken);

        String scope = StringUtils.hasText(scopeOverride) ? scopeOverride : refreshToken.getScope();
        String subject = StringUtils.hasText(claims.subject()) ? claims.subject() : String.valueOf(claims.userId());

        var newRefresh = jwtIssuer.issueRefreshToken(session.getUserId(), subject, session.getId(), refreshToken.getRotationIndex() + 1, scope);

        RefreshToken nextToken = RefreshToken.issue(session, newRefresh.tokenHash(), newRefresh.rotationIndex(), newRefresh.scope(), newRefresh.issuedAt(), newRefresh.expiresAt());
        nextToken.setPresentedFingerprint(presentedFingerprint);
        session.addRefreshToken(nextToken);

        refreshTokenRepository.save(nextToken);
        refreshToken.linkReplacedBy(nextToken.getId());

        var accessToken = jwtIssuer.issueAccessToken(session.getUserId(), subject, session.getId());
        AccessTokenJti accessEntity = AccessTokenJti.issue(accessToken.jti(), session.getUserId(), session.getId(), accessToken.issuedAt(), accessToken.expiresAt());
        accessTokenJtiRepository.save(accessEntity);

        var rotateDetail = new HashMap<String, Object>();
        rotateDetail.put("oldRefreshId", refreshToken.getId());
        rotateDetail.put("newRefreshId", nextToken.getId());
        rotateDetail.put("clientId", session.getClientId());
        rotateDetail.put("ip", clientIp);
        rotateDetail.put("kid", accessToken.kid());
        if (scope != null) {
            rotateDetail.put("scope", scope);
        }
        if (presentedFingerprint != null) {
            rotateDetail.put("fingerprint", Base64.getEncoder().encodeToString(presentedFingerprint));
        }
        tokenAuditService.record(session.getUserId(), session.getId(), nextToken.getId(), TokenEventType.ROTATE, rotateDetail);

        return new TokenResponse(
                AuthTokenUtils.TOKEN_TYPE_BEARER,
                accessToken.token(),
                accessToken.expiresAt(),
                newRefresh.token(),
                newRefresh.expiresAt(),
                session.getId()
        );
    }

}
