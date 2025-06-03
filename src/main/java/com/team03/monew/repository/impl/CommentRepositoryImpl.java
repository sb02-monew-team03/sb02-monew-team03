package com.team03.monew.repository.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.entity.Comment;
import com.team03.monew.entity.QComment;
import com.team03.monew.repository.custom.CommentRepositoryCustom;
import com.team03.monew.util.OrderBy;
import com.team03.monew.util.SortDirection;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findByArticleWithCursorPaging(
            Long articleId,
            OrderBy orderBy,
            SortDirection direction,
            Long cursor,
            LocalDateTime after,
            int limit,
            Long requesterId
    ){
        QComment comment = QComment.comment;

        CommentCursorWhere where = new CommentCursorWhere(articleId, orderBy, direction, after);
        OrderSpecifier<?> sort = CommentCursorSort.resolve(orderBy, direction);

        return queryFactory
                .selectFrom(comment)
                .where(where.build())
                .orderBy(sort)
                .limit(limit)
                .fetch();
    }
}
