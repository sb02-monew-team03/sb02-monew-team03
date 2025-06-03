package com.team03.monew.util;

import com.team03.monew.entity.Comment;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.CommentRepository;
import com.team03.monew.repository.NewsArticleRepository;
import com.team03.monew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityFinder {

    private final UserRepository userRepository;
    private final NewsArticleRepository newsArticleRepository;
    private final CommentRepository commentRepository;

    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("Long", "userId", userId.toString()),
                        ExceptionType.USER
                ));
    }

    public NewsArticle getNewsArticleOrThrow(Long articleId) {
        return newsArticleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("Long", "articleId", articleId.toString()),
                        ExceptionType.NEWSARTICLE
                ));
    }

    public Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("Long", "commentId", commentId.toString()),
                        ExceptionType.COMMENT
                ));
    }
}
