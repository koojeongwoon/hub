package com.tinyquest.hub.auth.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
  - email과 password는 필수 (@NotBlank, 이메일 형식)이고 나머지는 선택
  - clientId: 어떤 앱/클라이언트에서 로그인하는지 구분용. 보내지 않으면 서버가 "default"로 저장해서 세션 분리를 못 하니까, 웹·모바일 등 구분하고 싶으면 명시.
  - deviceName: 사용자가 보는 디바이스명. 비우면 "unknown"으로 남아 세션 목록에서 구분 어려움.
  - deviceFingerprint: 기기를 안정적으로 식별할 수 있는 바이트 배열을 Base64 등으로 인코딩해 전달해. 서버에서 Base64 decode 시도 후 실패하면 그냥 문자열 바이트를 쓰니까 가능하면 Base64로 보내는 게 안전.
  - scope: 필요한 권한 범위가 있을 때만 채우면 되고, 없으면 null 처리되며 토큰에 포함되지 않음.
      1. JwtIssuer.issueAccessToken에도 scope claim을 심고(scp), JwtVerifier.verifyAccessToken이 읽어오게 확장.
      2. AuthPrincipal 혹은 SecurityContext에 scope를 함께 실어서 요청 처리 시 인가 판단에 사용.
      3. 또는 CurrentUserProvider 같은 shared 포트를 통해 scope를 꺼내 다른 모듈에서 활용.
  - 클라이언트는 POST /api/auth/login에 위 필드들을 JSON으로 보내고, 응답으로 받은 accessToken, refreshToken, sessionId를 이후 호출·갱신·선택적 로그아웃에 활용.
 */
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        String clientId,
        String deviceName,
        String deviceFingerprint,
        String scope
) {}
