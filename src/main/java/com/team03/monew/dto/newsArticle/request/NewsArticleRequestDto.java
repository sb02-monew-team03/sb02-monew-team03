package com.team03.monew.dto.newsArticle.request;

import com.team03.monew.entity.NewsArticle;
import java.time.LocalDateTime;

public record NewsArticleRequestDto(
    String title,
    String originalLink,
    String summary,
    LocalDateTime date,
    String source
) {
    public NewsArticle toEntity() {
        return NewsArticle.builder()
            .title(title)
            .originalLink(originalLink)
            .summary(summary)
            .date(date)
            .source(source)
            .deleted(false)
            .viewCount(0)
            .build();
    }
}
