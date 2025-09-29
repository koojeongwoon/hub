package com.tinyquest.hub.notification.infra.dlq;

import com.tinyquest.hub.notification.domain.DeliveryCommand;
import com.tinyquest.hub.notification.domain.port.NotificationDlqWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileNotificationDlqWriter implements NotificationDlqWriter {

    private static final Logger log = LoggerFactory.getLogger(FileNotificationDlqWriter.class);

    private final Path dlqPath;
    private final ReentrantLock lock = new ReentrantLock();

    public FileNotificationDlqWriter(@Value("${notification.dlq.path:build/notification-dlq.log}") String dlqPath) {
        this.dlqPath = Path.of(dlqPath);
    }

    @Override
    public void write(String messageId, DeliveryCommand command, String reason, Throwable cause) {
        lock.lock();
        try {
            Path parent = dlqPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            String line = String.format("%s|%s|%s|%s|%s|%s%n",
                    Instant.now(),
                    messageId,
                    command.channel(),
                    command.recipient(),
                    command.payload().getClass().getSimpleName(),
                    reason != null ? reason : "UNKNOWN");
            Files.writeString(dlqPath, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ex) {
            log.error("DLQ 파일 기록 실패 path={} cause={}", dlqPath, ex.getMessage(), ex);
        } finally {
            lock.unlock();
        }
    }

    public Path dlqPath() {
        return dlqPath;
    }
}
