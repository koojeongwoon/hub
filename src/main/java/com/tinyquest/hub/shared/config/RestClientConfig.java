package com.tinyquest.hub.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.DefaultBackoffStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Slf4j
@Configuration
public class RestClientConfig {

    // 커넥션 풀 관련 설정값
    private static final int MAX_TOTAL_CONNECTIONS = 200; // 최대 커넥션 수 (전체)
    private static final int MAX_CONNECTIONS_PER_ROUTE = 20; // 호스트별 최대 커넥션 수
    private static final int MAX_IDLE_TIME = 60; // 유휴 상태 커넥션 유지 시간 (초)

    // 타임아웃 설정값 ( 연결 타임아웃 < 쓰기/읽기 타임아웃 ≤ 응답 타임아웃 )
    private static final long CONNECTION_REQUEST_TIMEOUT = 10L; // 커넥션 풀에서 커넥션을 가져오는 대기 시간 (초) - 커넥션 풀이 꽉 찬 경우 발생
    private static final long RESPONSE_TIMEOUT = 60L; // 서버 응답 대기 시간 (초) - 연결 후 응답을 기다리는 시간

    // 재시도 관련 설정값
    private static final int MAX_RETRIES = 3; // 요청 실패 시 최대 재시도 횟수
    private static final long INITIAL_RETRY_INTERVAL = 5; // 첫 번째 재시도 대기 시간 (초) - 이후 점진적 증가


    private static final int VALIDATE_INACTIVITY_TIME = 30;

    private static final long SOCKET_TIMEOUT = 60L;

    @Bean
    public RestClient restClient(HttpClient httpClient) {
        return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClients.custom()
                .setConnectionManager(buildConnectionManager())
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setRetryStrategy(buildRetryStrategy())
                .setConnectionBackoffStrategy(new DefaultBackoffStrategy())
                .setDefaultRequestConfig(requestConfig())
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofSeconds(MAX_IDLE_TIME))
                .build();
    }

    private PoolingHttpClientConnectionManager buildConnectionManager() {
        final ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setValidateAfterInactivity(TimeValue.ofSeconds(VALIDATE_INACTIVITY_TIME))
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
        connectionManager.setDefaultConnectionConfig(connectionConfig);
        connectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(SOCKET_TIMEOUT)).build());
        return connectionManager;
    }

    private RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(CONNECTION_REQUEST_TIMEOUT))
                .setResponseTimeout(Timeout.ofSeconds(RESPONSE_TIMEOUT))
                .build();
    }

    private HttpRequestRetryStrategy buildRetryStrategy() {
        return new DefaultHttpRequestRetryStrategy(MAX_RETRIES, TimeValue.ofSeconds(INITIAL_RETRY_INTERVAL)) {
            @Override
            public boolean retryRequest(HttpRequest request, IOException exception, int execCount, HttpContext context) {
                if (super.retryRequest(request, exception, execCount, context)) {
                    log.info("Retrying request to {} attempt {}", request.getRequestUri(), execCount);
                    return true;
                }
                log.warn("Max retries exceeded for request to {}", request.getRequestUri());
                return false;
            }
        };
    }
}
