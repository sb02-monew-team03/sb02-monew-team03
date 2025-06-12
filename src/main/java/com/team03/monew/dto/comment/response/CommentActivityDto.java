package com.team03.monew.dto.comment.response;

import com.team03.monew.entity.Comment;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommentActivityDto(
    UUID id,
    UUID articleId,
    String articleTitle,
    UUID userId,
    String userNickname,
    String content,
    long likeCount,
    LocalDateTime createdAt
) {

    public static CommentActivityDto from(Comment comment) {
        return new CommentActivityDto(
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
