package com.team03.monew.dto.newsArticle.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record NewsArticleRequestDto(
    String title,
    String originalLink,
    String summary,
    LocalDateTime date,
    String source,
    UUID interestId
) {}

