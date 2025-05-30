package com.team03.monew.util;

import com.team03.monew.entity.Comment;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.CommentRepositoryCustom;
import com.team03.monew.repository.NewsArticleRepository;
import com.team03.monew.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityFinder {

    private final UserRepository userRepository;
    private final NewsArticleRepository newsArticleRepository;
    private final CommentRepositoryCustom commentRepository;

    public User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "userId", userId.toString()),
                        ExceptionType.USER
                ));
    }

    public NewsArticle getNewsArticleOrThrow(UUID articleId) {
        return newsArticleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "articleId", articleId.toString()),
                        ExceptionType.NEWSARTICLE
                ));
    }

    public Comment getCommentOrThrow(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "commentId", commentId.toString()),
                        ExceptionType.COMMENT
                ));
    }
}
