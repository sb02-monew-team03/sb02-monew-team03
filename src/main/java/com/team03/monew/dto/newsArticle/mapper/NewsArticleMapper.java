package com.team03.monew.dto.newsArticle.mapper;

import com.team03.monew.dto.newsArticle.response.ArticleDto;
import com.team03.monew.entity.NewsArticle;

public class NewsArticleMapper {

    public static ArticleDto toArticleDto(NewsArticle news) {
        return new ArticleDto(
            news.getId(),
            news.getSource(),
            news.getOriginalLink(),             // sourceUrl
            news.getTitle(),
            news.getDate(),                     // publishDate
            news.getSummary(),
            news.getComments().size(),          // commentCount
            news.getViewCount(),
            false                                // viewedByMe (기본값 false)
        );
    }

}
