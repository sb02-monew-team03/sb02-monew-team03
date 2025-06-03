package com.team03.monew.dto.comment.mapper;

import com.team03.monew.dto.comment.response.CommentDto;
import com.team03.monew.entity.Comment;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.entity.User;

public class CommentMapper {

    public static Comment toComment(String content, NewsArticle article, User user) {
        return Comment.builder()
                .news(article)
                .user(user)
                .content(content)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getNews().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getLikeCount(),
                true,
                comment.getCreatedAt()
        );
    }
}
