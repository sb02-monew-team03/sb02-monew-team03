package com.team03.monew.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.entity.QInterest;
import com.team03.monew.entity.QNewsArticle;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.custom.NewsArticleRepositoryCustom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NewsArticleRepositoryImpl implements NewsArticleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<NewsArticle> searchArticles(
        String keyword,
        UUID interestId,
        List<String> sourceIn,
        LocalDateTime publishDateFrom,
        LocalDateTime publishDateTo,
        String orderBy,
        String direction,
        String after,
        int limit
    ) {
        QNewsArticle article = QNewsArticle.newsArticle;
        QInterest interest = QInterest.interest;
        BooleanBuilder condition = new BooleanBuilder();
        condition.and(article.deleted.isFalse());

        if (keyword != null && !keyword.isBlank()) {
            condition.and(article.title.containsIgnoreCase(keyword)
                .or(article.summary.containsIgnoreCase(keyword)));
        }
        if (interestId != null) {
            condition.and(interest.id.eq(interestId));
        }
        if (sourceIn != null && !sourceIn.isEmpty()) {
            condition.and(article.source.in(sourceIn));
        }
        if (publishDateFrom != null) {
            condition.and(article.date.goe(publishDateFrom));
        }
        if (publishDateTo != null) {
            condition.and(article.date.loe(publishDateTo));
        }
        if (after != null) {
            condition.and(article.date.lt(LocalDateTime.parse(after)));
        }

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(orderBy, direction);

        return queryFactory
            .selectFrom(article)
            .leftJoin(article.interests, interest)
            .where(condition)
            .orderBy(orderSpecifier)
            .limit(limit + 1)
            .fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(String orderBy, String direction) {
        QNewsArticle article = QNewsArticle.newsArticle;
        Order order = direction.equalsIgnoreCase("ASC") ? Order.ASC : Order.DESC;

        return switch (orderBy) {
            case "date" -> new OrderSpecifier<>(order, article.date);
            case "commentCount" -> new OrderSpecifier<>(order, article.comments.size());
            case "viewCount" -> new OrderSpecifier<>(order, article.viewCount);
            default -> throw new CustomException(ErrorCode.INVALID_TYPE_VALUE, new ErrorDetail("date, commentCount, vieCount", "orderBy", orderBy), ExceptionType.NEWSARTICLE);
        };
    }
}