package com.team03.monew.repository;

import com.team03.monew.entity.ArticleView;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleViewRepository extends JpaRepository<ArticleView, Long> {
    Optional<ArticleView> findByArticleIdAndUserId(Long articleId, Long userId);
}
