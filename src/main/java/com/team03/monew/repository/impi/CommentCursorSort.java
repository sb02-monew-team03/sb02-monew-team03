package com.team03.monew.repository.impi;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.team03.monew.entity.QComment;
import com.team03.monew.util.SortDirection;
import com.team03.monew.util.OrderBy;

public class CommentCursorSort {

    public static OrderSpecifier<?> resolve(OrderBy orderBy, SortDirection sortDirection) {
        QComment comment = QComment.comment;
        Order querydslOrder = sortDirection.toQuerydslOrder();

        if (orderBy == OrderBy.LIKE_COUNT) {
            return new OrderSpecifier<>(querydslOrder, comment.likeCount);
        } else {
            return new OrderSpecifier<>(querydslOrder, comment.createdAt);
        }
    }
}
