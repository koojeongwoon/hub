package com.tinyquest.hub.shared.response;

import java.time.Clock;
import java.time.Instant;

/**
 * API 응답을 위한 공통 형식입니다.
 * sealed interface로 구현하여, Success, Failure 등 정해진 타입의 응답만 반환되도록 강제합니다.
 * @param <T> 응답 데이터의 타입
 */
public sealed interface Response<T> permits Response.Success, Response.Failure {

    Clock CLOCK_UTC = Clock.systemUTC();

    record Success<T>(String code, String message, T data, Instant timestamp)
            implements Response<T> {
        public static <T> Success<T> of(T data) {
            return new Success<>("OK", "success", data, Instant.now(CLOCK_UTC));
        }

        public static Success<Void> of() {
            return new Success<>("OK", "success", null, Instant.now(CLOCK_UTC));
        }
    }

    record Failure<T>(String code, String message, T data, Instant timestamp)
            implements Response<T> {

        public static Failure<Void> of(String code, String message) {
            return new Failure<>(code, message, null, Instant.now(CLOCK_UTC));
        }
    }

}
