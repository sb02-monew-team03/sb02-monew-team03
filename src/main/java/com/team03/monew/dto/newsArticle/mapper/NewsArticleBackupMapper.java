package com.team03.monew.dto.newsArticle.mapper;


import com.team03.monew.dto.newsArticle.response.NewsArticleBackupDto;
import com.team03.monew.entity.Interest;
import com.team03.monew.entity.NewsArticle;
import java.util.List;
import java.util.UUID;

public class NewsArticleBackupMapper {

    public static NewsArticleBackupDto toDto(NewsArticle article) {
        List<UUID> interestIds = article.getInterests() != null
            ? article.getInterests().stream()
            .map(Interest::getId)
            .toList()
            : List.of(); // null이 아니라 빈 리스트로 처리

        return new NewsArticleBackupDto(
            article.getTitle(),
            article.getSummary(),
            article.getOriginalLink(),
            article.getSource(),
            article.getDate(),
            interestIds,
            article.getViewCount(),
            article.isDeleted()
        );
    }
}