package com.team03.monew.service;

import com.team03.monew.dto.comment.mapper.CommentMapper;
import com.team03.monew.dto.comment.request.CommentRegisterRequest;
import com.team03.monew.dto.comment.response.CommentDto;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NewsArticleRepository newsArticleRepository;


    @Override
    public CommentDto registerComment(CommentRegisterRequest commentRegisterRequest) {
        User user = userRepository.findById(commentRegisterRequest.userId())
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "userId", ExceptionType.USER.toString()),
                        ExceptionType.USER
                ));

        NewsArticle newsArticle = newsArticleRepository.findById(commentRegisterRequest.articleId())
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "articleId", ExceptionType.NEWSARTICLE.toString()),
                        ExceptionType.NEWSARTICLE
                ));
        Comment comment = CommentMapper.toComment(commentRegisterRequest.comment(), newsArticle, user);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }


}
