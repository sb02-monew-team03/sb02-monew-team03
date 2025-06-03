package com.team03.monew.dto.newsArticle.response;

import java.time.LocalDateTime;
import java.util.UUID;

// 백업된 json 파일을 읽어서 매핑하기 위한 dto
public record NewsBackupDto (
    UUID id,
    String source,
    String originalLink,
    String title,
    LocalDateTime date,
    String summary,
    UUID interestId
) {}
