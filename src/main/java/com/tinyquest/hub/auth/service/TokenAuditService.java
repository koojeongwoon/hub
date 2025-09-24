package com.tinyquest.hub.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyquest.hub.auth.constants.TokenEventType;
import com.tinyquest.hub.auth.domain.entity.TokenAudit;
import com.tinyquest.hub.auth.domain.repository.TokenAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenAuditService {

    private final TokenAuditRepository tokenAuditRepository;
    private final ObjectMapper objectMapper;

    public void record(Long userId, UUID sessionId, UUID refreshId, TokenEventType eventType, Map<String, Object> detail) {
        String json = toJson(detail);
        TokenAudit audit = TokenAudit.of(userId, sessionId, refreshId, eventType, json);
        tokenAuditRepository.save(audit);
    }

    private String toJson(Map<String, Object> detail) {
        Map<String, Object> safeDetail = detail == null ? Collections.emptyMap() : detail;
        try {
            return objectMapper.writeValueAsString(safeDetail);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
