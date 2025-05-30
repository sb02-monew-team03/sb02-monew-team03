package com.team03.monew.dto.newsArticle;

import java.time.LocalDateTime;
import java.util.List;

public record ArticleRestoreResultDto(
    LocalDateTime restoreDate,
    List<String> restoredArticleIds,
    int restoredArticleCount
) {}
