package com.team03.monew.repository.custom;

import com.team03.monew.entity.NewsArticle;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NewsArticleRepositoryCustom {
    List<NewsArticle> searchArticles(
        String keyword,
        UUID interestId,
        List<String> sourceIn,
        LocalDateTime publishDateFrom,
        LocalDateTime publishDateTo,
        String orderBy,
        String direction,
        String after,
        int limit
    );
}