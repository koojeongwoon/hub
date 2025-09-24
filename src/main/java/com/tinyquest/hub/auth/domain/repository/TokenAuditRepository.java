package com.tinyquest.hub.auth.domain.repository;

import com.tinyquest.hub.auth.domain.entity.TokenAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TokenAuditRepository extends JpaRepository<TokenAudit, Long> {

    List<TokenAudit> findTop50ByUserIdOrderByCreatedAtDesc(Long userId);

    List<TokenAudit> findTop50BySessionIdOrderByCreatedAtDesc(UUID sessionId);
}
