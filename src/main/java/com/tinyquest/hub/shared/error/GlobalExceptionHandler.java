package com.tinyquest.hub.shared.error;

import com.tinyquest.hub.shared.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public ApiResponse.Failure<?> handleBusinessException(BusinessException ex) {
        return ApiResponse.Failure.of(ex.getErrorCode().getCode(), ex.getErrorCode().getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse.Failure<Void> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        return ApiResponse.Failure.of("VALIDATION_ERROR", "요청 값이 유효하지 않습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiResponse.Failure<Void> handleBind(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(v -> errors.put(v.getField(), v.getDefaultMessage()));
        return ApiResponse.Failure.of("BIND_ERROR", "바인딩 중 오류가 발생했습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse.Failure<Void> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        return ApiResponse.Failure.of("CONSTRAINT_VIOLATION", "제약 조건 위반입니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse.Failure<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ApiResponse.Failure.of("TYPE_MISMATCH", "파라미터 타입이 올바르지 않습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse.Failure<Void> handleNotReadable(HttpMessageNotReadableException ex) {
        return ApiResponse.Failure.of("INVALID_JSON", "요청 본문(JSON) 파싱에 실패했습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResponse.Failure<Void> handleMissingParam(MissingServletRequestParameterException ex) {
        Map<String, String> errors = Map.of(ex.getParameterName(), "필수 파라미터가 누락되었습니다.");
        return ApiResponse.Failure.of("MISSING_PARAMETER", "필수 파라미터가 누락되었습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingPathVariableException.class)
    public ApiResponse.Failure<Void> handleMissingPathVar(MissingPathVariableException ex) {
        return ApiResponse.Failure.of("MISSING_PATH_VARIABLE", "필수 경로 변수가 누락되었습니다.");
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse.Failure<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ApiResponse.Failure.of("METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다.");
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ApiResponse.Failure<Void> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return ApiResponse.Failure.of("UNSUPPORTED_MEDIA_TYPE", "지원하지 않는 Content-Type 입니다.");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse.Failure<Void> handleOthers(Exception ex) {
        log.error("Unhandled exception", ex);
        return ApiResponse.Failure.of("INTERNAL_SERVER_ERROR", "예상치 못한 오류가 발생했습니다.");
    }
}
