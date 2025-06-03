package com.team03.monew.dto.comment.response;


public record CommentLikeActivityDto(Long id, Long createdBy, Long commentId, Long articleId, String articledTitle, Long commentUserId, String commentUserNickName, String commentContent, Integer commentLikeCount, String commentCreatedAt) {
}
