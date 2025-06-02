package com.team03.monew.service;

import com.team03.monew.dto.notification.CursorPageResponseNotificationDto;
import com.team03.monew.dto.notification.NotificationDto;
import com.team03.monew.entity.Notification;
import com.team03.monew.repository.NotificationRepository;
import com.team03.monew.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  @Transactional
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

  @Transactional
  public void confirmAll(UUID userId) {
    notificationRepository.updateAllCheckedByUserId(userId);
  }

  @Transactional
  public void confirm(UUID notificationId, UUID userId) {
    Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
        .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
    notification.setChecked(true);
    notification.setUpdatedAt(LocalDateTime.now());
  }
}
