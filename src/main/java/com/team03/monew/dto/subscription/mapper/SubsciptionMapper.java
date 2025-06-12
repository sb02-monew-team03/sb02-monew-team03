package com.team03.monew.dto.subscription.mapper;

import com.team03.monew.dto.subscription.SubscriptionDto;
import com.team03.monew.entity.Interest;
import com.team03.monew.entity.Subscription;

public class SubsciptionMapper {

  public static SubscriptionDto from(Subscription subscription) {
    Interest interest = subscription.getInterest();
    return new SubscriptionDto(
        subscription.getId(),
        interest.getId(),
        interest.getName(),
        interest.getKeywords(),
        interest.getSubscriberCount(),
        subscription.getCreatedAt()
    );
  }

}
