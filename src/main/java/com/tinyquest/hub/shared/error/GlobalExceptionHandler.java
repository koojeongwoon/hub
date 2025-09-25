package com.tinyquest.hub.shared.error;

import com.tinyquest.hub.shared.response.Response;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
import java.util.Locale;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public Response.Failure<?> handleBusinessException(BusinessException ex) {
        String message = resolveMessage(ex.getErrorCode().getMessageKey(), ex.getMessageArguments(), ex.getOverrideMessage());
        return Response.Failure.of(ex.getErrorCode().getCode(), message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response.Failure<Void> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        return Response.Failure.of("VALIDATION_ERROR", resolveMessage("error.generic.bad-request"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Response.Failure<Void> handleBind(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(v -> errors.put(v.getField(), v.getDefaultMessage()));
        return Response.Failure.of("BIND_ERROR", resolveMessage("error.generic.bind"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Response.Failure<Void> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        return Response.Failure.of("CONSTRAINT_VIOLATION", resolveMessage("error.generic.constraint"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Response.Failure<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return Response.Failure.of("TYPE_MISMATCH", resolveMessage("error.generic.type-mismatch"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response.Failure<Void> handleNotReadable(HttpMessageNotReadableException ex) {
        return Response.Failure.of("INVALID_JSON", resolveMessage("error.generic.invalid-json"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response.Failure<Void> handleMissingParam(MissingServletRequestParameterException ex) {
        Map<String, String> errors = Map.of(ex.getParameterName(), resolveMessage("error.generic.missing-parameter"));
        return Response.Failure.of("MISSING_PARAMETER", resolveMessage("error.generic.missing-parameter"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingPathVariableException.class)
    public Response.Failure<Void> handleMissingPathVar(MissingPathVariableException ex) {
        return Response.Failure.of("MISSING_PATH_VARIABLE", resolveMessage("error.generic.missing-path-variable"));
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response.Failure<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return Response.Failure.of("METHOD_NOT_ALLOWED", resolveMessage("error.generic.method-not-allowed"));
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Response.Failure<Void> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return Response.Failure.of("UNSUPPORTED_MEDIA_TYPE", resolveMessage("error.generic.unsupported-media-type"));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Response.Failure<Void> handleOthers(Exception ex) {
        log.error("Unhandled exception", ex);
        return Response.Failure.of("INTERNAL_SERVER_ERROR", resolveMessage("error.generic.unexpected"));
    }

    private String resolveMessage(String key) {
        return resolveMessage(key, null, null);
    }

    private String resolveMessage(String key, Object[] args, String defaultMessage) {
        Locale locale = LocaleContextHolder.getLocale();
        String fallback = defaultMessage != null ? defaultMessage : key;
        return messageSource.getMessage(key, args, fallback, locale);
    }
}
