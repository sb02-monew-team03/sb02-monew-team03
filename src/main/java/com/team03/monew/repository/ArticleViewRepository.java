package com.team03.monew.repository;

import com.team03.monew.entity.ArticleView;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.entity.User;
import com.team03.monew.repository.custom.ArticleViewRepositoryCustom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleViewRepository extends JpaRepository<ArticleView, UUID>,
    ArticleViewRepositoryCustom {

    Optional<ArticleView> findByArticleAndUser(NewsArticle article, User user);

    void deleteByArticle(NewsArticle article);

    List<ArticleView> findTop10ByUserOrderByCreatedAtDesc(User user);
}
