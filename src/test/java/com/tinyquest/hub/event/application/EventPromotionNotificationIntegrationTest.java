package com.tinyquest.hub.event.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.tinyquest.hub.event.domain.entity.Event;
import com.tinyquest.hub.event.domain.entity.EventEntry;
import com.tinyquest.hub.event.domain.entity.EventWinner;
import com.tinyquest.hub.event.service.EventService;
import com.tinyquest.hub.notification.domain.DeliveryCommand;
import com.tinyquest.hub.notification.domain.entity.NotificationLog;
import com.tinyquest.hub.notification.domain.port.EmailGateway;
import com.tinyquest.hub.notification.domain.port.NotificationChannelAdapter;
import com.tinyquest.hub.notification.domain.repository.NotificationLogRepository;
import com.tinyquest.hub.notification.service.NotificationCampaignService;
import com.tinyquest.hub.promotion.domain.PromotionType;
import com.tinyquest.hub.promotion.domain.entity.Promotion;
import com.tinyquest.hub.promotion.domain.entity.PromotionBenefit;
import com.tinyquest.hub.promotion.domain.entity.PromotionCoupon;
import com.tinyquest.hub.promotion.service.PromotionService;
import com.tinyquest.hub.shared.port.notification.ChannelPreference;
import com.tinyquest.hub.shared.port.notification.NotificationRecipient;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@ActiveProfiles("h2")
@Import(EventPromotionNotificationIntegrationTest.NotificationTestConfig.class)
class EventPromotionNotificationIntegrationTest {

    @TempDir
    static Path tempDir;

    @Autowired
    private EventService eventService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Autowired
    private NotificationCaptureAdapter captureAdapter;

    @Autowired
    private TestEmailGateway testEmailGateway;

    @Autowired
    private NotificationCampaignService notificationCampaignService;

    private static Path dlqPath;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        dlqPath = tempDir.resolve("notification-dlq.log");
        registry.add("notification.dlq.path", () -> dlqPath.toString());
        registry.add("notification.retry.max-attempts", () -> 3);
    }

    @BeforeEach
    void setUp() {
        captureAdapter.enable();
        captureAdapter.clear();
        notificationLogRepository.deleteAll();
        testEmailGateway.reset();
        try {
            Files.deleteIfExists(dlqPath);
        } catch (IOException ignored) {
        }
    }

    @Test
    @DisplayName("행사 도메인 흐름이 알림 로그와 채널 명령을 생성한다")
    void eventLifecycleCreatesNotifications() {
        Event event = eventService.create("봄맞이 추첨", "DRAW");

        eventService.publish(
                event.getId(),
                ChannelPreference.emailOnly(),
                List.of(NotificationRecipient.of(200L, "user@example.com", null))
        );

        EventEntry entry = eventService.participate(
                event.getId(),
                200L,
                ChannelPreference.emailOnly(),
                List.of(NotificationRecipient.of(200L, "user@example.com", null))
        );

        EventWinner winner = eventService.selectWinner(
                event.getId(),
                entry.getId(),
                300L,
                1,
                ChannelPreference.emailOnly(),
                List.of(NotificationRecipient.of(200L, "user@example.com", null))
        );

        assertThat(event.getId()).isNotNull();
        assertThat(entry.getId()).isNotNull();
        assertThat(winner.getId()).isNotNull();

        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                assertThat(captureAdapter.commands())
                        .hasSize(3)
                        .allMatch(command -> command.channel().equalsIgnoreCase("EMAIL"))
        );

        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            List<NotificationLog> logs = notificationLogRepository.findAll();
            assertThat(logs)
                    .hasSize(3)
                    .allMatch(log -> "EMAIL".equalsIgnoreCase(log.getChannel()));
        });
    }

    @Test
    @DisplayName("프로모션 도메인 흐름이 알림 로그와 채널 명령을 생성한다")
    void promotionLifecycleCreatesNotifications() {
        Promotion promotion = promotionService.create("여름 쿠폰", PromotionType.COUPON, 900L);

        promotionService.activate(
                promotion.getId(),
                ChannelPreference.smsOnly(),
                List.of(NotificationRecipient.of(400L, null, "01012345678"))
        );

        PromotionCoupon coupon = promotionService.issueCoupon(
                promotion.getId(),
                "ABC-123",
                400L,
                Instant.now().plusSeconds(3600),
                ChannelPreference.smsOnly(),
                List.of(NotificationRecipient.of(400L, null, "01012345678"))
        );

        PromotionBenefit benefit = promotionService.applyBenefit(
                promotion.getId(),
                BigDecimal.valueOf(5000),
                "KRW",
                "ORDER-1",
                "POINT",
                ChannelPreference.smsOnly(),
                List.of(NotificationRecipient.of(400L, null, "01012345678"))
        );

        assertThat(coupon.getId()).isNotNull();
        assertThat(benefit.getId()).isNotNull();

        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                assertThat(captureAdapter.commands())
                        .hasSize(3)
                        .allMatch(command -> command.channel().equalsIgnoreCase("SMS"))
        );

        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            List<NotificationLog> logs = notificationLogRepository.findAll();
            assertThat(logs)
                    .hasSize(3)
                    .allMatch(log -> "SMS".equalsIgnoreCase(log.getChannel()));
        });
    }

    @Test
    @DisplayName("채널 발송이 3회 실패하면 DLQ에 기록한다")
    void notificationFailureFallsBackToDlq() {
        captureAdapter.disable();
        testEmailGateway.failNext(3);

        Event event = eventService.create("실패 알림", "DRAW");

        eventService.publish(
                event.getId(),
                ChannelPreference.emailOnly(),
                List.of(NotificationRecipient.of(1L, "fail@example.com", null))
        );

        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                assertThat(testEmailGateway.attempts()).isEqualTo(3)
        );

        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            List<NotificationLog> logs = notificationLogRepository.findAll();
            assertThat(logs)
                    .hasSize(1)
                    .allMatch(log -> "EMAIL".equalsIgnoreCase(log.getChannel())
                            && log.getErrorMessage() != null);
        });

        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                assertThat(Files.exists(dlqPath)).isTrue()
        );

        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            List<String> lines = Files.readAllLines(dlqPath);
            assertThat(lines).isNotEmpty();
        });
    }

    @Test
    @DisplayName("마케팅 캠페인 발송이 비동기로 처리된다")
    void marketingCampaignDispatchesNotifications() {
        captureAdapter.enable();
        notificationCampaignService.launchCampaign(
                "봄맞이 프로모션",
                "봄맞이 쿠폰 도착",
                "지금 접속하면 추가 혜택!",
                ChannelPreference.emailOnly(),
                List.of(NotificationRecipient.of(500L, "marketing@example.com", null))
        );

        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                assertThat(captureAdapter.commands())
                        .isNotEmpty()
                        .allMatch(command -> command.channel().equalsIgnoreCase("EMAIL"))
        );

        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            List<NotificationLog> logs = notificationLogRepository.findAll();
            assertThat(logs)
                    .isNotEmpty()
                    .anyMatch(log -> "EMAIL".equalsIgnoreCase(log.getChannel())
                            && "EventNotificationPayload".equals(log.getPayloadType()) == false);
        });
    }

    @TestConfiguration
    static class NotificationTestConfig {

        @Bean
        @Primary
        @Order(0)
        NotificationCaptureAdapter notificationCaptureAdapter() {
            return new NotificationCaptureAdapter();
        }

        @Bean
        @Primary
        TestEmailGateway testEmailGateway() {
            return new TestEmailGateway();
        }
    }

    static class NotificationCaptureAdapter implements NotificationChannelAdapter, Ordered {

        private final CopyOnWriteArrayList<DeliveryCommand> commands = new CopyOnWriteArrayList<>();
        private volatile boolean intercept = true;

        @Override
        public boolean supports(String channel) {
            return intercept;
        }

        @Override
        public void send(DeliveryCommand command) {
            if (intercept) {
                commands.add(command);
            }
        }

        @Override
        public int getOrder() {
            return 0;
        }

        void clear() {
            commands.clear();
        }

        List<DeliveryCommand> commands() {
            return List.copyOf(commands);
        }

        void disable() {
            intercept = false;
        }

        void enable() {
            intercept = true;
        }
    }

    static class TestEmailGateway implements EmailGateway {

        private final AtomicInteger attempts = new AtomicInteger();
        private volatile int failureThreshold = 0;

        @Override
        public void send(DeliveryCommand command) {
            int currentAttempt = attempts.incrementAndGet();
            if (currentAttempt <= failureThreshold) {
                throw new RuntimeException("intentional-email-failure-" + currentAttempt);
            }
        }

        void failNext(int attempts) {
            this.failureThreshold = attempts;
            this.attempts.set(0);
        }

        void reset() {
            this.failureThreshold = 0;
            this.attempts.set(0);
        }

        int attempts() {
            return attempts.get();
        }
    }
}
