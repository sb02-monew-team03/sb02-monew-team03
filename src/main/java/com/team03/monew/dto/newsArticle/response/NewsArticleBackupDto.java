package com.team03.monew.dto.newsArticle.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NewsArticleBackupDto(
    UUID id, // 복구 판단용, JPA에서 사용 x
    String title,
    String summary,
    String originalLink,
    String source,
    LocalDateTime date,
    UUID interestId,
    int viewCount,
    boolean deleted
    // 댓글, 댓글 좋아요에 대한 내요은 변동성이 강하기에 일단 제외. 현재는 뉴스 기사 자체에 집중
    // List<CommentBackupDto> comments
) {}
