package com.team03.monew.dto.newsArticle.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ArticleRestoreResultDto(
    LocalDateTime restoreDate,
    List<UUID> restoredArticleIds,
    int restoredArticleCount
) {}
