package com.team03.monew.dto.comment.response;

import java.time.LocalDateTime;

public record CommentLikeDto(
        Long id,
        Long likedBy,
        LocalDateTime createdBy,
        Long commentId,
        Long articleId,
        Long commentUserId,
        String commentUserNickName,
        String commentContent,
        Integer commentLikeCount,
        LocalDateTime commentCreatedAt
) {}
