package com.tinyquest.hub.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PageableConfig {
    @Bean
    PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {
        return resolver -> {
            resolver.setOneIndexedParameters(false);
            resolver.setMaxPageSize(100);
            resolver.setFallbackPageable(
                    PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt"))
            );
        };
    }
}
