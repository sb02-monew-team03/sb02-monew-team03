package com.team03.monew.dto.comment.mapper;

import com.team03.monew.dto.comment.response.CommentDto;
import com.team03.monew.entity.Comment;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.entity.User;

public class CommentMapper {

        public Comment toComment(CommentDto dto, NewsArticle article, User user) {
            return Comment.builder()
                    .news(article)
                    .user(user)
                    .content(dto.content())
                    .likeCount(dto.likeCount())
                    .build();
        }

//    public CommentDto toCommentDto(Comment comment) {
//        return new CommentDto(
//                comment.getId().toString(),
//                comment.getNews().getId().toString(),
//                comment.getUser().getId().toString(),
//                comment.getUser().getNickname(),
//                comment.getContent(),
//                comment.getLikeCount(),
//                false,
//                comment.getCreatedAt()
//        );
//    }

}
