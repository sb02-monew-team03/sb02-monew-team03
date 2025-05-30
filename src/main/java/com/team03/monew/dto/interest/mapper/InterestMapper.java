package com.team03.monew.dto.interest.mapper;

import com.team03.monew.dto.interest.InterestDto;
import com.team03.monew.dto.interest.InterestRegisterRequest;
import com.team03.monew.entity.Interest;


public class InterestMapper {

  public static InterestDto toDto(Interest interest) {
    return new InterestDto(
        interest.getId(),
        interest.getName(),
        interest.getKeywords(),
        interest.getSubscriberCount(),
        false
    );
  }

  public static Interest toEntity(InterestRegisterRequest request) {
    return Interest.builder()
        .name(request.name())
        .keywords(request.keywords())
        .build();
  }
}

