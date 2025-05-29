package com.team03.monew.dto.notification;

import com.team03.monew.entity.Notification;
import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean confirmed,
    UUID userId,
    String content,
    String resourceType,
    UUID resourceId
) {
  public static NotificationDto from(Notification notification) {
    return new NotificationDto(
        notification.getId(),
        notification.getCreatedAt(),
        notification.getUpdatedAt(),
        notification.isChecked(),
        notification.getUser().getId(),
        notification.getContent(),
        notification.getRelatedType().name().toLowerCase(),
        notification.getRelatedId()
    );
  }
}
