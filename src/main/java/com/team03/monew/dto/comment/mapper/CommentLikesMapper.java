package com.team03.monew.dto.comment.mapper;

import com.team03.monew.dto.comment.request.CommentLikeDto;
import com.team03.monew.entity.Comment;
import com.team03.monew.entity.CommentLike;
import com.team03.monew.entity.User;

public class CommentLikesMapper {

    public static CommentLike toCommentLike(Comment comment, User user) {
        return CommentLike.builder()
                .comment(comment)
                .user(user)
                .deleted(false)
                .build();
    }


    public static CommentLikeDto toCommentLikeDto(CommentLike commentLike) {
        Comment comment = commentLike.getComment();
        User commentAuthor = comment.getUser();

        return new CommentLikeDto(
                commentLike.getId(),
                commentLike.getUser().getId(),
                commentLike.getCreatedAt(),
                comment.getId(),
                comment.getNews().getId(),
                commentAuthor.getId(),
                commentAuthor.getNickname(),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getCreatedAt()
        );
    }
}
