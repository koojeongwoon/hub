package com.tinyquest.hub.notification.service;

import com.tinyquest.hub.notification.domain.entity.NotificationLog;
import com.tinyquest.hub.notification.domain.repository.NotificationLogRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationLogRepository notificationLogRepository;

    public List<NotificationLog> findLatest(int size) {
        return notificationLogRepository.findAll(
                PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();
    }
}
