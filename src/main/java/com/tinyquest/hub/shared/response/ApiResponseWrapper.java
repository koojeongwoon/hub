package com.tinyquest.hub.shared.response;

import com.tinyquest.hub.shared.meta.NoWrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Optional;

@Slf4j
@ControllerAdvice(basePackages = "com.tinyquest.hub")
public class ApiResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (returnType.getContainingClass().isAnnotationPresent(NoWrap.class)
                || (returnType.getMethod() != null && returnType.getMethod().isAnnotationPresent(NoWrap.class))) {
            return false;
        }
        return !ApiResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(
            Object body, 
            MethodParameter returnType, 
            MediaType selectedContentType, 
            Class<? extends HttpMessageConverter<?>> selectedConverterType, 
            ServerHttpRequest request, 
            ServerHttpResponse response
    ) {
        final HttpMethod method = request.getMethod();

        if (body instanceof Resource
                || body instanceof byte[]
                || "text/event-stream".equalsIgnoreCase(String.valueOf(selectedContentType))) {
            return body;
        }

        if (body instanceof ResponseEntity<?> entity) {
            Object origin = entity.getBody();
            HttpStatusCode status = entity.getStatusCode();
            HttpHeaders headers = HttpHeaders.readOnlyHttpHeaders(entity.getHeaders());

            if (origin instanceof ApiResponse<?>) {
                return entity;
            }

            if (method == HttpMethod.POST) {
                if (origin == null) {
                    return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(ApiResponse.Success.of(null));
                }
                if (!headers.containsKey(HttpHeaders.LOCATION)) {
                    tryExtractId(origin).ifPresent(id -> {
                        headers.setLocation(buildLocationUri(request, id));
                    });
                }
                if (headers.containsKey(HttpHeaders.LOCATION) && status.equals(HttpStatus.OK)) {
                    status = HttpStatus.CREATED;
                }
            } else if (method == HttpMethod.PUT || method == HttpMethod.PATCH || method == HttpMethod.DELETE) {
                if (origin == null && (status.equals(HttpStatus.OK) || status.equals(HttpStatus.NO_CONTENT))) {
                    return ResponseEntity.ok().headers(headers).body(ApiResponse.Success.of(null));
                }
            }

            return ResponseEntity.status(status).headers(headers).body(ApiResponse.Success.of(origin));
        }

        if (body == null) {
            return ApiResponse.Success.of(null);
        }

        if (method == HttpMethod.POST) {
            if (!response.getHeaders().containsKey(HttpHeaders.LOCATION)) {
                tryExtractId(body).ifPresent(id -> {
                    response.getHeaders().setLocation(buildLocationUri(request, id));
                    response.setStatusCode(HttpStatus.CREATED);
                });
            } else {
                response.setStatusCode(HttpStatus.CREATED);
            }
        }

        return ApiResponse.Success.of(body);
    }

    private Optional<String> tryExtractId(Object body) {
        try {
            Method m = body.getClass().getMethod("getId");
            Object v = m.invoke(body);
            if (v != null) return Optional.of(String.valueOf(v));
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            return Optional.empty();
        }
        try {
            if (body.getClass().isRecord()) {
                var comps = body.getClass().getRecordComponents();
                for (var c : comps) {
                    if ("id".equals(c.getName())) {
                        Object v = c.getAccessor().invoke(body);
                        if (v != null) return Optional.of(String.valueOf(v));
                    }
                }
            }
        } catch (Exception ignored) {}

        try {
            var f = body.getClass().getDeclaredField("id");
            f.setAccessible(true);
            Object v = f.get(body);
            if (v != null) return Optional.of(String.valueOf(v));
        } catch (NoSuchFieldException ignored) {
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    private URI buildLocationUri(ServerHttpRequest request, String id) {
        String path = request.getURI().getPath();
        if (StringUtils.hasText(path) && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String base = request.getURI().getScheme() + "://" + request.getHeaders().getHost() + path;
        return URI.create(base + "/" + id);
    }
}
