package com.team03.monew.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.team03.monew.entity.QComment;
import com.team03.monew.util.SortDirection;
import com.team03.monew.util.OrderBy;
import java.time.LocalDateTime;


public class CommentCursorWhere {

    private final Long articleId;
    private final OrderBy orderBy;
    private final SortDirection sortDirection;
    private final LocalDateTime after;

    public  CommentCursorWhere(Long articleId, OrderBy orderBy, SortDirection sortDirection, LocalDateTime after) {
        this.articleId = articleId;
        this.orderBy = orderBy;
        this.sortDirection = sortDirection;
        this.after = after;
    }

    public BooleanBuilder build() {
        QComment comment = QComment.comment;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(comment.news.id.eq(articleId));

        if (after != null && orderBy == OrderBy.CREATED_AT) {
            if (sortDirection == SortDirection.ASC) {
                builder.and(comment.createdAt.gt(after));
            } else if (sortDirection == SortDirection.DESC) {
                builder.and(comment.createdAt.lt(after));
            }
        }

        return builder;
    }
}
