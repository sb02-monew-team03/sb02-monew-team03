package com.team03.monew.dto.junhyeok;

import java.time.LocalDateTime;
import java.util.List;

public record CursorPageResponseNotificationDto(
    List<NotificationDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {}