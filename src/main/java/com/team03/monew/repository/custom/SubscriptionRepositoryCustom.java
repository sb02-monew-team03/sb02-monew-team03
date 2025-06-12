package com.team03.monew.repository.custom;

import com.team03.monew.entity.Subscription;
import com.team03.monew.entity.User;
import java.util.List;

public interface SubscriptionRepositoryCustom {

    List<Subscription> findByUserFetchInterest(User user);
}
