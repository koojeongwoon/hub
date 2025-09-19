package com.tinyquest.hub.shared.constants;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // ===== COMMON =====
    INTERNAL_SERVER_ERROR("COMMON:INTERNAL:5001", "서버 내부 오류가 발생했습니다."),

    // ===== USER =====
    USER_VALIDATION_1001("USER:VALIDATION:1001", "이메일 형식이 올바르지 않습니다."),
    USER_VALIDATION_1002("USER:VALIDATION:1002", "이미 사용 중인 이메일입니다."),
    USER_AUTH_2001("USER:AUTH:2001", "인증 토큰이 유효하지 않습니다."),
    USER_NOT_FOUND_4001("USER:NOT_FOUND:4001", "사용자를 찾을 수 없습니다."),

    // ===== PRODUCT =====
    PRODUCT_VALIDATION_1001("PRODUCT:VALIDATION:1001", "가격이 음수일 수 없습니다."),
    PRODUCT_NOT_FOUND_4001("PRODUCT:NOT_FOUND:4001", "상품을 찾을 수 없습니다."),
    PRODUCT_INTERNAL_5001("PRODUCT:INTERNAL:5001", "상품 저장 실패");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
