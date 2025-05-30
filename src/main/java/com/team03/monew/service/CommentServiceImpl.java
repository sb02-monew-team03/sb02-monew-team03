package com.team03.monew.service;

import com.team03.monew.dto.comment.mapper.CommentLikesMapper;
import com.team03.monew.dto.comment.mapper.CommentMapper;
import com.team03.monew.dto.comment.response.CommentLikeDto;
import com.team03.monew.dto.comment.request.CommentRegisterRequest;
import com.team03.monew.dto.comment.request.CommentUpdateRequest;
import com.team03.monew.dto.comment.response.CommentDto;
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
import com.team03.monew.repository.OrderBy;
import com.team03.monew.repository.SortDirection;
import com.team03.monew.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

    @Transactional(readOnly = true)
    public CursorPageResponseCommentDto<Comment> commentCursorPage(
            UUID articleId,
            OrderBy orderBy,
            SortDirection direction,
            UUID cursor,
            LocalDateTime after,
            int limit,
            UUID requesterId
    ) {

        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "userId", ExceptionType.USER.toString()),
                        ExceptionType.USER
                ));

        NewsArticle newsArticle = newsArticleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "articleId", ExceptionType.NEWSARTICLE.toString()),
                        ExceptionType.NEWSARTICLE
                ));


        List<Comment> comments = commentRepository.findByArticleWithCursorPaging(
                articleId, orderBy, direction, cursor, after, limit, requesterId
        );

        boolean hasNext = comments.size() == limit;
        LocalDateTime nextAfter = null;
        UUID nextCursor = null;
        if (hasNext) {
            Comment last = comments.get(comments.size() - 1);
            nextCursor = last.getId();
            nextAfter = last.getCreatedAt();
        }

        if (nextCursor != null) {
            nextCursor = nextCursor;
        }else {
            nextCursor = null;
        }
        return CursorPageResponseCommentDto.<Comment>builder()
                .content(comments)
                .nextCursor(nextCursor.toString())
                .nextAfter(nextAfter)
                .size(limit)
                .totalElements(comments.size())
                .hasNext(hasNext)
                .build();
    }


    @Override
    @Transactional
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

    @Override
    @Transactional
    public CommentLikeDto commentLikes(UUID commentId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "userId", ExceptionType.USER.toString()),
                        ExceptionType.USER
                ));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "commentId", ExceptionType.COMMENT.toString()),
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
        return CommentLikesMapper.toCommentLikeDto(commentLikeRepository.save(commentLike));
    }

    @Override
    @Transactional
    public void cancelCommentLike(UUID commentId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "userId", userId.toString()),
                        ExceptionType.USER
                ));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "commentId", commentId.toString()),
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
    @Transactional
    public void softDeleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "commentId", commentId.toString()),
                        ExceptionType.COMMENT
                ));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(
                    ErrorCode.FORBIDDEN,
                    new ErrorDetail("UUID", "userId", userId.toString()),
                    ExceptionType.USER
            );
        }

        comment.markAsDeleted();
    }

    @Override
    @Transactional
    public void hardDeleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "commentId", commentId.toString()),
                        ExceptionType.COMMENT
                ));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(
                    ErrorCode.FORBIDDEN,
                    new ErrorDetail("UUID", "userId", userId.toString()),
                    ExceptionType.USER
            );
        }

        commentRepository.delete(comment); // 실제 DB 삭제
    }

    @Override
    @Transactional
    public CommentDto updateComment(UUID commentId, UUID userId, CommentUpdateRequest commentUpdateRequest) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("UUID", "commentId", commentId.toString()),
                        ExceptionType.COMMENT
                ));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(
                    ErrorCode.FORBIDDEN,
                    new ErrorDetail("UUID", "userId", userId.toString()),
                    ExceptionType.USER
            );
        }

        if (comment.isDeleted()) {
            throw new CustomException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    new ErrorDetail("Boolean", "deleted", "true"),
                    ExceptionType.COMMENT
            );
        }

        comment.updateContent(commentUpdateRequest.content());
        return CommentMapper.toCommentDto(comment);
    }

}
