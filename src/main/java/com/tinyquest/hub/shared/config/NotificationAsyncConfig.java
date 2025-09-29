package com.tinyquest.hub.shared.config;

import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableAsync
@EnableRetry
public class NotificationAsyncConfig {

    @Bean(name = "notificationTaskExecutor")
    public TaskExecutor notificationTaskExecutor(
            @Value("${notification.executor.core-size:2}") int coreSize,
            @Value("${notification.executor.max-size:4}") int maxSize,
            @Value("${notification.executor.queue-capacity:100}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("notification-dispatch-");
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(Math.max(coreSize, maxSize));
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public RetryTemplate notificationRetryTemplate(
            @Value("${notification.retry.max-attempts:3}") int maxAttempts,
            @Value("${notification.retry.backoff:PT0.2S}") Duration backoff) {
        long backoffMillis = Math.max(backoff.toMillis(), 10L);
        return RetryTemplate.builder()
                .maxAttempts(Math.max(maxAttempts, 1))
                .fixedBackoff(backoffMillis)
                .retryOn(Exception.class)
                .build();
    }
}
