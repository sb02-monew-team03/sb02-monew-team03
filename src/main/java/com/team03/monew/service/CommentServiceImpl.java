package com.team03.monew.service;

import com.team03.monew.dto.comment.mapper.CommentLikesMapper;
import com.team03.monew.dto.comment.mapper.CommentMapper;
import com.team03.monew.dto.comment.request.CommentRegisterRequest;
import com.team03.monew.dto.comment.request.CommentUpdateRequest;
import com.team03.monew.dto.comment.response.CommentDto;
import com.team03.monew.dto.comment.response.CommentLikeDto;
import com.team03.monew.dto.comment.response.CursorPageResponseCommentDto;
import com.team03.monew.entity.Comment;
import com.team03.monew.entity.CommentLike;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.CommentLikeRepository;
import com.team03.monew.repository.CommentRepository;
import com.team03.monew.repository.NewsArticleRepository;
import com.team03.monew.repository.UserRepository;
import com.team03.monew.util.OrderBy;
import com.team03.monew.util.SortDirection;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final NewsArticleRepository newsArticleRepository;


    @Override
    public CursorPageResponseCommentDto<CommentDto> commentCursorPage(Long articleId, OrderBy orderBy,
                                                                      SortDirection direction, Long cursor,
                                                                      LocalDateTime after, int limit,
                                                                      Long requesterId) {
        return null;
    }

    @Override
    @Transactional
    public CommentDto registerComment(CommentRegisterRequest commentRegisterRequest) {
        User user = userRepository.findById(commentRegisterRequest.userId())
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("Long", "userId", ExceptionType.USER.toString()),
                        ExceptionType.USER
                ));

        NewsArticle newsArticle = newsArticleRepository.findById(commentRegisterRequest.articleId())
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("Long", "articleId", ExceptionType.NEWSARTICLE.toString()),
                        ExceptionType.NEWSARTICLE
                ));
        Comment comment = CommentMapper.toComment(commentRegisterRequest.comment(), newsArticle, user);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentLikeDto commentLikes(Long commentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("Long", "userId", ExceptionType.USER.toString()),
                        ExceptionType.USER
                ));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("Long", "commentId", ExceptionType.COMMENT.toString()),
                        ExceptionType.COMMENT
                ));

        if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
            throw new CustomException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    new ErrorDetail("RELATION", "comment-user", commentId + "/" + userId),
                    ExceptionType.COMMENT
            );
        }

        CommentLike commentLike = CommentLikesMapper.toCommentLike(comment, user);

        comment.addCommentLike(commentLike); // 연관관계 편의 메서드
        // 좋아요 수 증가
        comment.increaseLikeCount();
        CommentLike save = commentLikeRepository.save(commentLike);
        return CommentLikesMapper.toCommentLikeDto(save);
    }

    @Override
    @Transactional
    public void cancelCommentLike(Long commentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("Long", "userId", userId.toString()),
                        ExceptionType.USER
                ));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("Long", "commentId", commentId.toString()),
                        ExceptionType.COMMENT
                ));

        CommentLike commentLike = commentLikeRepository.findByCommentAndUser(comment, user)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("RELATION", "comment-user", commentId + "/" + userId),
                        ExceptionType.COMMENT
                ));

        // 좋아요 취소 처리
        comment.removeCommentLike(commentLike); // 연관관계 정리
        comment.decreaseLikeCount();
        commentLikeRepository.delete(commentLike);
    }

    @Override
    public void softDeleteComment(Long commentId, Long userId) {

    }

    @Override
    public void hardDeleteComment(Long commentId, Long userId) {

    }

    @Override
    public CommentDto updateComment(Long commentId, Long userId, CommentUpdateRequest commentUpdateRequest) {
        return null;
    }

    public int getLikeCount(long commentId) {
        Integer likeCountById = commentRepository.findLikeCountById(commentId);
        if (likeCountById == null){
            return 0;
        }else{
            return likeCountById;
        }
    }
}
