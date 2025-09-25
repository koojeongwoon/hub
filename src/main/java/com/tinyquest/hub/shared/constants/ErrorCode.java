package com.tinyquest.hub.shared.constants;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // ===== COMMON =====
    INTERNAL_SERVER_ERROR("COMMON:INTERNAL:5001", "error.common.internal"),
    IP_ERROR("COMMON:INTERNAL:5010", "error.common.ip"),

    // ===== USER =====
    USER_VALIDATION_1001("USER:VALIDATION:1001", "error.user.validation.invalid-email"),
    USER_VALIDATION_1002("USER:VALIDATION:1002", "error.user.validation.email-duplicated"),
    USER_AUTH_2001("USER:AUTH:2001", "error.user.auth.invalid-token"),
    USER_AUTH_2002("USER:AUTH:2002", "error.user.auth.forbidden"),
    USER_NOT_FOUND_4001("USER:NOT_FOUND:4001", "error.user.not-found"),
    AUTH_SESSION_NOT_FOUND_4002("AUTH:SESSION:4002", "error.auth.session.not-found"),

    // ===== PRODUCT =====
    PRODUCT_VALIDATION_1001("PRODUCT:VALIDATION:1001", "error.product.validation.negative-price"),
    PRODUCT_NOT_FOUND_4001("PRODUCT:NOT_FOUND:4001", "error.product.not-found"),
    PRODUCT_INTERNAL_5001("PRODUCT:INTERNAL:5001", "error.product.internal.save-failed");

    private final String code;
    private final String messageKey;

    ErrorCode(String code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }
}
