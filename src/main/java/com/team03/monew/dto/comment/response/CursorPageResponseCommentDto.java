package com.team03.monew.dto.comment.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;


@Builder
public record CursorPageResponseCommentDto<T>(
        List<T> content, String nextCursor, LocalDateTime nextAfter, int size, long totalElements, boolean hasNext
) {
}
