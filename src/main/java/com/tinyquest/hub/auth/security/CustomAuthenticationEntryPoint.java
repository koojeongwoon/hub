package com.tinyquest.hub.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyquest.hub.shared.constants.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8"); // <- 추가
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        Map<String, Object> body = new HashMap<>();
        body.put("code", ErrorCode.USER_AUTH_2001);
        body.put("message", ErrorCode.USER_AUTH_2001.getMessage());
        body.put("data", null);
        body.put("timestamp", OffsetDateTime.now());

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
