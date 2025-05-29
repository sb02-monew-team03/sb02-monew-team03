package com.team03.monew.dto.comment.request;

import java.util.UUID;

public record CommentLikeActivityDto(UUID id, UUID createdBy, UUID commentId, UUID articleId, String articledTitle, UUID commentUserId, String commentUserNickName, String commentContent, Integer commentLikeCount, String commentCreatedAt) {
}
