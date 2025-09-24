package com.tinyquest.hub.auth.support;

import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class AuthTokenUtils {

    private AuthTokenUtils() {
    }

    public static final String TOKEN_TYPE_BEARER = "Bearer";

    public static byte[] decodeFingerprint(String fingerprint) {
        if (!StringUtils.hasText(fingerprint)) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(fingerprint);
        } catch (IllegalArgumentException ex) {
            return fingerprint.getBytes(StandardCharsets.UTF_8);
        }
    }
}
