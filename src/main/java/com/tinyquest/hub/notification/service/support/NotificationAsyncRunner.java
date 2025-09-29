package com.tinyquest.hub.notification.service.support;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class NotificationAsyncRunner {

    private final TaskExecutor notificationTaskExecutor;

    public NotificationAsyncRunner(@Qualifier("notificationTaskExecutor") TaskExecutor notificationTaskExecutor) {
        this.notificationTaskExecutor = notificationTaskExecutor;
    }

    public void runAsync(Runnable task) {
        Runnable safeTask = Objects.requireNonNull(task, "task");
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    notificationTaskExecutor.execute(safeTask);
                }
            });
        } else {
            notificationTaskExecutor.execute(safeTask);
        }
    }
}
