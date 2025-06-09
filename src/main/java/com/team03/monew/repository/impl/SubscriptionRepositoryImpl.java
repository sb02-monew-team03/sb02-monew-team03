package com.team03.monew.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.entity.QInterest;
import com.team03.monew.entity.QSubscription;
import com.team03.monew.entity.Subscription;
import com.team03.monew.entity.User;
import com.team03.monew.repository.custom.SubscriptionRepositoryCustom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Subscription> findByUserFetchInterest(User user) {
        QSubscription subscription = QSubscription.subscription;
        QInterest interest = QInterest.interest;

        return queryFactory
            .selectFrom(subscription)
            .join(subscription.interest, interest).fetchJoin()  // 즉시 로딩 설정
            .where(subscription.user.eq(user))
            .orderBy(subscription.createdAt.desc())
            .fetch();
    }
}
