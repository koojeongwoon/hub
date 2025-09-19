package com.tinyquest.hub.shared.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class ValidationUtils {

    private ValidationUtils() {}

    // 날짜 형식 정규식 (yyyy-MM-dd HH:mm:ss)
    private static final Pattern DATE_PATTERN = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 공통 필드 유효성 검사 (null 또는 빈 값)
     * @param value 필드 값
     * @param fieldName 필드 이름
     * @param lineNumber 데이터 라인 번호
     * @return 유효성 검사 실패 메시지 또는 null
     */
    public static String checkNullOrEmpty(Object value, String fieldName, int lineNumber) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return String.format("라인 %d: 필드 '%s'가 비어있거나 null입니다.", lineNumber, fieldName);
        }
        return null;
    }

    /**
     * 숫자 형식 유효성 검사 (쉼표 제거 후 숫자 여부 확인)
     * @param value 필드 값
     * @param fieldName 필드 이름
     * @param lineNumber 데이터 라인 번호
     * @return 유효성 검사 실패 메시지 또는 null
     */
    public static String checkNumeric(Object value, String fieldName, int lineNumber) {
        String numericValue = value.toString().trim();
        if (!StringUtils.isNumeric(numericValue)) {
            return String.format("라인 %d: 필드 '%s'의 값이 잘못된 숫자 형식입니다: %s", lineNumber, fieldName, value);
        }
        return null;
    }

    /**
     * Nullable 숫자 형식 유효성 검사 (쉼표 제거 후 숫자 여부 확인)
     * - null 또는 빈 문자열은 통과
     * - 값이 있을 경우 숫자 형식이어야 함
     *
     * @param value 필드 값
     * @param fieldName 필드 이름
     * @param lineNumber 데이터 라인 번호
     * @return 유효성 검사 실패 메시지 또는 null
     */
    public static String checkNullableNumeric(Object value, String fieldName, int lineNumber) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return null; // null 또는 공백은 통과
        }

        return checkNumeric(value, fieldName, lineNumber);
    }

    /**
     * 날짜 형식 유효성 검사 (yyyy-MM-dd HH:mm:ss)
     * @param value 필드 값
     * @param fieldName 필드 이름
     * @param lineNumber 데이터 라인 번호
     * @return 유효성 검사 실패 메시지 또는 null
     */
    public static String checkDateFormat(Object value, String fieldName, int lineNumber) {
        String dateValue = value.toString();

        // 1. 정규식으로 형식 검증
        if (!DATE_PATTERN.matcher(dateValue).matches()) {
            return String.format("라인 %d: 필드 '%s'의 값이 잘못된 날짜 형식입니다: %s", lineNumber, fieldName, dateValue);
        }

        // 2. 정확한 날짜 파싱 검증
        try {
            LocalDateTime.parse(dateValue, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return String.format("라인 %d: 필드 '%s'의 값이 날짜로 변환할 수 없습니다: %s", lineNumber, fieldName, dateValue);
        }
        return null;
    }
}
