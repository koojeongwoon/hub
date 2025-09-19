package com.tinyquest.hub.shared.utils;

public class SqlStringUtils {

    private SqlStringUtils() {
    }

    /**
     * SQL용 문자열 escape 처리.
     * 홑따옴표(')를 두 개('')로 치환하여 SQL 인젝션 방지
     *
     * @param input 사용자 입력 문자열
     * @return SQL-safe 문자열
     */
    public static String escape(String input) {
        return input == null ? "" : input.replace("'", "''");
    }

}
