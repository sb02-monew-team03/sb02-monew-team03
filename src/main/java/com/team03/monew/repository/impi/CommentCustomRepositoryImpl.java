package com.team03.monew.repository.impi;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.entity.Comment;
import com.team03.monew.entity.QComment;
import com.team03.monew.repository.Custom.CommentCustomRepository;
import com.team03.monew.repository.OrderBy;
import com.team03.monew.repository.SortDirection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findByArticleWithCursorPaging(
            UUID articleId,
            OrderBy orderBy,
            SortDirection direction,
            UUID cursor,
            LocalDateTime after,
            int limit,
            UUID requesterId
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
