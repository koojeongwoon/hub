package com.tinyquest.hub.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyquest.hub.shared.constants.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;
    private final Clock clock;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        String message = messageSource.getMessage(
                ErrorCode.USER_AUTH_2001.getMessageKey(),
                null,
                ErrorCode.USER_AUTH_2001.getMessageKey(),
                LocaleContextHolder.getLocale()
        );

        Map<String, Object> body = new HashMap<>();
        body.put("code", ErrorCode.USER_AUTH_2001.getCode());
        body.put("message", message);
        body.put("data", null);
        body.put("timestamp", Instant.now(clock));

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
