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
import com.team03.monew.util.OrderBy;
import com.team03.monew.util.SortDirection;
import com.team03.monew.util.EntityFinder;
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
    private final EntityFinder entityFinder;

    @Transactional(readOnly = true)
    public CursorPageResponseCommentDto<CommentDto> commentCursorPage(
            UUID articleId,
            OrderBy orderBy,
            SortDirection direction,
            UUID cursor,
            LocalDateTime after,
            int limit,
            UUID requesterId
    ) {
        entityFinder.getUserOrThrow(requesterId);
        entityFinder.getNewsArticleOrThrow(articleId);

        List<Comment> comments = commentRepository.findByArticleWithCursorPaging(
                articleId, orderBy, direction, cursor, after, limit, requesterId
        );

        List<CommentDto> commentDtoList = comments.stream()
                .map(CommentMapper::toCommentDto)
                .toList();

        boolean hasNext = comments.size() == limit;
        LocalDateTime nextAfter = null;
        UUID nextCursor = null;
        if (hasNext) {
            Comment last = comments.get(comments.size() - 1);
            nextCursor = last.getId();
            nextAfter = last.getCreatedAt();
        }

        return CursorPageResponseCommentDto.<CommentDto>builder()
                .content(commentDtoList)
                .nextCursor(nextCursor != null ? nextCursor.toString() : null)
                .nextAfter(nextAfter)
                .size(limit)
                .totalElements(comments.size())
                .hasNext(hasNext)
                .build();
    }

    @Override
    @Transactional
    public CommentDto registerComment(CommentRegisterRequest request) {
        User user = entityFinder.getUserOrThrow(request.userId());
        NewsArticle article = entityFinder.getNewsArticleOrThrow(request.articleId());

        Comment comment = CommentMapper.toComment(request.comment(), article, user);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentLikeDto commentLikes(UUID commentId, UUID userId) {
        User user = entityFinder.getUserOrThrow(userId);
        Comment comment = entityFinder.getCommentOrThrow(commentId);

        if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
            throw new CustomException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    new ErrorDetail("RELATION", "comment-user", commentId + "/" + userId),
                    ExceptionType.COMMENT
            );
        }

        CommentLike commentLike = CommentLikesMapper.toCommentLike(comment, user);
        comment.addCommentLike(commentLike);  // 연관관계 메서드
        comment.increaseLikeCount();

        return CommentLikesMapper.toCommentLikeDto(commentLikeRepository.save(commentLike));
    }

    @Override
    @Transactional
    public void cancelCommentLike(UUID commentId, UUID userId) {
        User user = entityFinder.getUserOrThrow(userId);
        Comment comment = entityFinder.getCommentOrThrow(commentId);

        CommentLike commentLike = commentLikeRepository.findByCommentAndUser(comment, user)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        new ErrorDetail("RELATION", "comment-user", commentId + "/" + userId),
                        ExceptionType.COMMENT
                ));

        comment.removeCommentLike(commentLike);
        comment.decreaseLikeCount();
        commentLikeRepository.delete(commentLike);
    }

    @Override
    @Transactional
    public void softDeleteComment(UUID commentId, UUID userId) {
        Comment comment = entityFinder.getCommentOrThrow(commentId);

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
        Comment comment = entityFinder.getCommentOrThrow(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(
                    ErrorCode.FORBIDDEN,
                    new ErrorDetail("UUID", "userId", userId.toString()),
                    ExceptionType.USER
            );
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(UUID commentId, UUID userId, CommentUpdateRequest request) {
        Comment comment = entityFinder.getCommentOrThrow(commentId);

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

        comment.updateContent(request.content());
        return CommentMapper.toCommentDto(comment);
    }
}
