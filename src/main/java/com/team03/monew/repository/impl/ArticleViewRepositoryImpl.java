package com.team03.monew.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.entity.ArticleView;
import com.team03.monew.entity.QArticleView;
import com.team03.monew.entity.QNewsArticle;
import com.team03.monew.entity.User;
import com.team03.monew.repository.custom.ArticleViewRepositoryCustom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleViewRepositoryImpl implements ArticleViewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ArticleView> findTop10ByUserWithArticle(User user) {
        QArticleView view = QArticleView.articleView;
        QNewsArticle article = QNewsArticle.newsArticle;

        return queryFactory
            .selectFrom(view)
            .join(view.article, article).fetchJoin()
            .where(view.user.eq(user))
            .orderBy(view.createdAt.desc())
            .limit(10)
            .fetch();
    }
}
