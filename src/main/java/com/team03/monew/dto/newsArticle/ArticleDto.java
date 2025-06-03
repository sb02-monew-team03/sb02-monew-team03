package com.team03.monew.dto.newsArticle;

import java.time.LocalDateTime;

public record ArticleDto(
    Long id,
    String source,
    String sourceUrl,
    String title,
    LocalDateTime publishDate,
    String summary,
    int commentCount,
    int viewCount,
    boolean viewedByMe
) {}