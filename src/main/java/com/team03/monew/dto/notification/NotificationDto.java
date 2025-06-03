package com.team03.monew.dto.notification;

import com.team03.monew.entity.Notification;
import java.time.LocalDateTime;


public record NotificationDto(
    Long id,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean confirmed,
    Long userId,
    String content,
    String resourceType,
    Long resourceId
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
