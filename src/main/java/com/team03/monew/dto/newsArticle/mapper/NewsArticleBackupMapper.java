package com.team03.monew.dto.newsArticle.mapper;


import com.team03.monew.dto.newsArticle.response.NewsArticleBackupDto;
import com.team03.monew.entity.NewsArticle;

public class NewsArticleBackupMapper {

    public static NewsArticleBackupDto toDto(NewsArticle article) {
        return new NewsArticleBackupDto(
            article.getId(),
            article.getTitle(),
            article.getSummary(),
            article.getOriginalLink(),
            article.getSource(),
            article.getDate(),
            article.getInterest() != null ? article.getInterest().getId() : null,
            article.getViewCount(),
            article.isDeleted()
        );
    }
}