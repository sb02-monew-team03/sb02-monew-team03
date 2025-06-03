package com.team03.monew.dto.newsArticle;

import java.time.LocalDateTime;

public record ArticleViewDto(
    Long id,
    Long viewedBy,
    LocalDateTime createdAt,
    Long articleId,
    String source,
    String sourceUrl,
    String articleTitle,
    LocalDateTime articlePublishedDate,
    String articleSummary,
    int articleCommentCount,
    int articleViewCount
) {}
