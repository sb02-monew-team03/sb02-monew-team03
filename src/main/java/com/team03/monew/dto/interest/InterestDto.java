package com.team03.monew.dto.interest;

import java.util.List;

// InterestDto.java
public record InterestDto(
    Long id,
    String name,
    List<String> keywords,
    long subscriberCount,
    boolean subscribedByMe
) {}
