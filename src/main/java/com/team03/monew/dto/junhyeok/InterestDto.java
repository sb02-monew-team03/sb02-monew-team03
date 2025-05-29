package com.team03.monew.dto.junhyeok;

import java.util.List;
import java.util.UUID;

// InterestDto.java
public record InterestDto(
    UUID id,
    String name,
    List<String> keywords,
    long subscriberCount,
    boolean subscribedByMe
) {}
