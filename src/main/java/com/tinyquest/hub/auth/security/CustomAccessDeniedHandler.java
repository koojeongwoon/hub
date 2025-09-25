package com.tinyquest.hub.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinyquest.hub.shared.constants.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;
    private final Clock clock;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        String message = messageSource.getMessage(
                ErrorCode.USER_AUTH_2002.getMessageKey(),
                null,
                ErrorCode.USER_AUTH_2002.getMessageKey(),
                LocaleContextHolder.getLocale()
        );

        Map<String, Object> body = new HashMap<>();
        body.put("code", ErrorCode.USER_AUTH_2002.getCode());
        body.put("message", message);
        body.put("data", null);
        body.put("timestamp", Instant.now(clock));

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
