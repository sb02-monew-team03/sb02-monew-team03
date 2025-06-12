package com.team03.monew.dto.interest;

import java.time.LocalDateTime;
import java.util.List;

// CursorPageResponseInterestDto.java
public record CursorPageResponseInterestDto(
    List<InterestDto> content,
    String nextCursor,
    LocalDateTime nextAfter,
    int size,
    long totalElements,
    boolean hasNext
) {}

