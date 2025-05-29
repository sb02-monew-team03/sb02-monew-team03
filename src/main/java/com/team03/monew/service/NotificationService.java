package com.team03.monew.service;

import com.team03.monew.dto.junhyeok.CursorPageResponseNotificationDto;
import com.team03.monew.dto.junhyeok.NotificationDto;
import com.team03.monew.entity.Notification;
import com.team03.monew.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;

  public CursorPageResponseNotificationDto getNotifications(UUID userId, UUID cursor, LocalDateTime after, int limit) {
    Pageable pageable = PageRequest.of(0, limit);
    List<Notification> notifications = notificationRepository.findByCursorAndAfter(userId, cursor, after, pageable);

    List<NotificationDto> content = notifications.stream()
        .map(NotificationDto::from)
        .toList();

    boolean hasNext = content.size() == limit;
    String nextCursor = hasNext ? content.get(content.size() - 1).id().toString() : null;
    LocalDateTime nextAfter = hasNext ? content.get(content.size() - 1).createdAt() : null;
    long totalElements = notificationRepository.countByUserId(userId);

    return new CursorPageResponseNotificationDto(
        content, nextCursor, nextAfter, limit, totalElements, hasNext
    );
  }
}
