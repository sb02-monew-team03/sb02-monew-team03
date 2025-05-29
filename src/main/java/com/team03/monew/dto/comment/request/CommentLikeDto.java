package com.team03.monew.dto.comment.request;

import java.io.Serializable;

public record CommentLikeDto(String id, String likedBy, String createdBy, String commentId, String articleId, String commentUserId, String commentUserNickName, String commentContent, Integer commentLikeCount, String commentCreatedAt)
         {
}
