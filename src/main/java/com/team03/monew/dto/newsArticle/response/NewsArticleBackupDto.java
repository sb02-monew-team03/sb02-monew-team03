package com.team03.monew.dto.newsArticle.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record NewsArticleBackupDto(
    String title,
    String summary,
    String originalLink,
    String source,
    LocalDateTime date,
    List<UUID> interestIds,
    int viewCount,
    boolean deleted
) {}
