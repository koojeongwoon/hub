package com.tinyquest.hub.shared.permission;

import java.util.Arrays;

/**
 * 리소스에 대해 수행할 수 있는 세부 동작 코드입니다.
 */
public enum PermissionAction {
    VIEW,
    CREATE,
    UPDATE,
    DELETE,
    APPROVE,
    EXECUTE;

    public static PermissionAction fromCode(String code) {
        return Arrays.stream(values())
                .filter(action -> action.name().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown permission action code: " + code));
    }
}
