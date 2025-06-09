package com.team03.monew.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.entity.CommentLike;
import com.team03.monew.entity.QComment;
import com.team03.monew.entity.QCommentLike;
import com.team03.monew.entity.QNewsArticle;
import com.team03.monew.entity.QUser;
import com.team03.monew.entity.User;
import com.team03.monew.repository.custom.CommentLikeRepositoryCustom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentLikeRepositoryImpl implements CommentLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentLike> findTop10ByUserWithCommentAndNewsAndUser(User user) {
        QCommentLike like = QCommentLike.commentLike;
        QComment comment = QComment.comment;
        QNewsArticle news = QNewsArticle.newsArticle;
        QUser commentUser = QUser.user;

        return queryFactory
            .selectFrom(like)
            .join(like.comment, comment).fetchJoin()
            .join(comment.news, news).fetchJoin()
            .join(comment.user, commentUser).fetchJoin()
            .where(like.user.eq(user))
            .orderBy(like.createdAt.desc())
            .limit(10)
            .fetch();
    }
}
