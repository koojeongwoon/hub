package com.tinyquest.hub.auth.service;

import com.tinyquest.hub.auth.constants.TokenEventType;
import com.tinyquest.hub.auth.domain.entity.AuthSession;
import com.tinyquest.hub.auth.domain.entity.RefreshToken;
import com.tinyquest.hub.auth.domain.repository.AccessTokenJtiRepository;
import com.tinyquest.hub.auth.domain.repository.AuthSessionRepository;
import com.tinyquest.hub.auth.domain.repository.RefreshTokenRepository;
import com.tinyquest.hub.shared.constants.ErrorCode;
import com.tinyquest.hub.shared.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenRevocationService {

    private static final String REUSE_REASON = "Refresh token reuse detected";

    private final AuthSessionRepository authSessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenJtiRepository accessTokenJtiRepository;
    private final TokenAuditService tokenAuditService;
    private final Clock clock;

    public void revokeSessionWithTokens(Long userId, UUID sessionId, String reason, boolean allDevices) {
        String normalizedReason = normalizeReason(reason, TokenEventType.LOGOUT.name());
        Instant when = revokeSessionOrThrow(userId, sessionId, normalizedReason);
        finalizeSessionRevocation(userId, sessionId, normalizedReason, when, allDevices);
    }

    public void revokeAllActiveSessions(Long userId, String reason) {
        String normalizedReason = normalizeReason(reason, TokenEventType.LOGOUT.name());
        List<AuthSession> sessions = authSessionRepository.findByUserIdAndRevokedAtIsNullOrderByCreatedAtDesc(userId);
        for (AuthSession session : sessions) {
            revokeSessionWithTokens(userId, session.getId(), normalizedReason, true);
        }
    }

    public void handleReuseDetection(AuthSession session, RefreshToken token, String clientIp) {
        Instant now = clock.instant();
        authSessionRepository.revokeSession(session.getId(), session.getUserId(), now, REUSE_REASON);
        session.revoke(REUSE_REASON, now);
        refreshTokenRepository.revokeAllBySessionId(session.getId(), now);
        accessTokenJtiRepository.revokeAllBySessionId(session.getId(), now, REUSE_REASON);

        Map<String, Object> detail = new HashMap<>();
        detail.put("refreshId", token.getId());
        detail.put("clientId", session.getClientId());
        detail.put("ip", clientIp);
        if (token.getPresentedFingerprint() != null) {
            detail.put("fingerprint", Base64.getEncoder().encodeToString(token.getPresentedFingerprint()));
        }
        if (token.getScope() != null) {
            detail.put("scope", token.getScope());
        }

        tokenAuditService.record(session.getUserId(), session.getId(), token.getId(), TokenEventType.REUSE_DETECTED, detail);
        tokenAuditService.record(session.getUserId(), session.getId(), null, TokenEventType.LOGOUT, logoutDetail(REUSE_REASON, true, session.getClientId()));
    }

    private Instant revokeSessionOrThrow(Long userId, UUID sessionId, String reason) {
        Instant now = clock.instant();
        int updated = authSessionRepository.revokeSession(sessionId, userId, now, reason);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.AUTH_SESSION_NOT_FOUND_4002);
        }
        return now;
    }

    private void finalizeSessionRevocation(Long userId, UUID sessionId, String reason, Instant when, boolean allDevices) {
        refreshTokenRepository.revokeAllBySessionId(sessionId, when);
        accessTokenJtiRepository.revokeAllBySessionId(sessionId, when, reason);
        tokenAuditService.record(userId, sessionId, null, TokenEventType.LOGOUT, logoutDetail(reason, allDevices, null));
    }

    private Map<String, Object> logoutDetail(String reason, boolean allDevices, String clientId) {
        Map<String, Object> detail = new HashMap<>();
        detail.put("reason", reason);
        detail.put("allDevices", allDevices);
        if (StringUtils.hasText(clientId)) {
            detail.put("clientId", clientId);
        }
        return detail;
    }

    private String normalizeReason(String reason, String fallback) {
        return StringUtils.hasText(reason) ? reason : fallback;
    }
}
