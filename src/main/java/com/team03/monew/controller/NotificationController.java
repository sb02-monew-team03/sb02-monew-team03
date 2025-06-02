package com.team03.monew.controller;

import com.team03.monew.dto.notification.CursorPageResponseNotificationDto;
import com.team03.monew.service.NotificationService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<CursorPageResponseNotificationDto> getNotifications(
      @RequestHeader("Monew-Request-User-ID") UUID userId,
      @RequestParam(required = false) UUID cursor,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
      @RequestParam(defaultValue = "50") int limit
  ) {
    CursorPageResponseNotificationDto result = notificationService.getNotifications(userId, cursor, after, limit);
    return ResponseEntity.ok(result);
  }


  @PatchMapping
  public ResponseEntity<Void> confirmAllNotifications(
      @RequestHeader("Monew-Request-User-ID") UUID userId
  ) {
    notificationService.confirmAll(userId);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{notificationId}")
  public ResponseEntity<Void> confirmNotification(
      @RequestHeader("Monew-Request-User-ID") UUID userId,
      @PathVariable UUID notificationId
  ) {
    notificationService.confirm(notificationId, userId);
    return ResponseEntity.ok().build();
  }
}
