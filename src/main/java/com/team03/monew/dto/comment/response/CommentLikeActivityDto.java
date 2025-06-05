package com.team03.monew.dto.comment.response;

import com.team03.monew.entity.Comment;
import com.team03.monew.entity.CommentLike;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommentLikeActivityDto(
    UUID id,
    LocalDateTime createdAt,
    UUID commentId,
    UUID articleId,
    String articledTitle,
    UUID commentUserId,
    String commentUserNickName,
    String commentContent,
    long commentLikeCount,
    LocalDateTime commentCreatedAt
) {

    public static CommentLikeActivityDto from(CommentLike commentLike) {
        Comment comment = commentLike.getComment();
        return new CommentLikeActivityDto(
            commentLike.getId(),
            commentLike.getCreatedAt(),
            comment.getId(),
            comment.getNews().getId(),
            comment.getNews().getTitle(),
            comment.getUser().getId(),
            comment.getUser().getNickname(),
            comment.getContent(),
            comment.getLikeCount(),
            comment.getCreatedAt()
        );
    }
}
