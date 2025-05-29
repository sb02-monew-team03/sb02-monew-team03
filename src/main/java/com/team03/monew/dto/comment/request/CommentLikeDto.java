package com.team03.monew.dto.comment.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentLikeDto(
        UUID id,
        UUID likedBy,
        LocalDateTime createdBy,
        UUID commentId,
        UUID articleId,
        UUID commentUserId,
        String commentUserNickName,
        String commentContent,
        Integer commentLikeCount,
        LocalDateTime commentCreatedAt
) {}
