package com.tinyquest.hub.auth.config;

import com.tinyquest.hub.auth.security.PermissionGuardInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class PermissionWebConfig implements WebMvcConfigurer {

    private final PermissionGuardInterceptor permissionGuardInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionGuardInterceptor)
                .addPathPatterns("/api/**")
                .order(1);
    }
}
