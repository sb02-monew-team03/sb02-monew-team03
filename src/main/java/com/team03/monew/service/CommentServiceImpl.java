package com.team03.monew.service;

import com.team03.monew.dto.comment.mapper.CommentLikesMapper;
import com.team03.monew.dto.comment.mapper.CommentMapper;
import com.team03.monew.dto.comment.request.CommentRegisterRequest;
import com.team03.monew.dto.comment.request.CommentUpdateRequest;
import com.team03.monew.dto.comment.response.CommentActivityDto;
import com.team03.monew.dto.comment.response.CommentDto;
import com.team03.monew.dto.comment.response.CommentLikeActivityDto;
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
import com.team03.monew.service.activity.ActivityDocumentUpdater;
import com.team03.monew.util.OrderBy;
import com.team03.monew.util.SortDirection;
import com.team03.monew.util.EntityFinder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final EntityFinder entityFinder;
    private final ActivityDocumentUpdater activityDocumentUpdater;

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

        // 댓글 생성 및 저장
        Comment comment = CommentMapper.toComment(request.content(), article, user);
        Comment savedComment = commentRepository.save(comment);

        // DTO 변환
        CommentDto commentDto = CommentMapper.toCommentDto(savedComment);

        // Mongo 활동 내역 동기화 (최근 댓글 추가)
        CommentActivityDto commentActivityDto = CommentActivityDto.from(savedComment);
        activityDocumentUpdater.addRecentComment(user.getId(), commentActivityDto);

        // 5. 응답 반환
        return commentDto;
    }

    @Override
    @Transactional
    public CommentLikeDto commentLikes(UUID commentId, UUID userId) {
        log.info("[CommentLike] 요청 들어옴 - commentId: {}, userId: {}", commentId, userId);

        User user = entityFinder.getUserOrThrow(userId);
        Comment comment = entityFinder.getCommentOrThrow(commentId);

        log.debug("[CommentLike] DB 조회된 user.id: {}, comment.id: {}", user.getId(), comment.getId());

        boolean alreadyLiked = commentLikeRepository.existsByCommentAndUser(comment, user);
        log.debug("[CommentLike] existsByCommentAndUser 결과: {}", alreadyLiked);

        if (alreadyLiked) {
            log.warn("[CommentLike] 중복 좋아요 시도 감지 - commentId: {}, userId: {}", commentId, userId);
            throw new CustomException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    new ErrorDetail("RELATION", "comment-user", commentId + "/" + userId),
                    ExceptionType.COMMENT
            );
        }

        CommentLike commentLike = CommentLikesMapper.toCommentLike(comment, user);
        log.debug("[CommentLike] 새로운 좋아요 객체 생성됨 - commentLikeId: {}", commentLike.getId());

        comment.increaseLikeCount();
        log.debug("[CommentLike] 좋아요 수 증가 - 현재 likeCount: {}", comment.getLikeCount());

        CommentLike saved = commentLikeRepository.save(commentLike);
        log.info("[CommentLike] 저장 완료 - commentLikeId: {}, userId: {}, commentId: {}", saved.getId(), userId, commentId);

        // Mongo 활동 내역 동기화
        CommentLikeActivityDto activityDto = CommentLikeActivityDto.from(saved);
        activityDocumentUpdater.addRecentCommentLike(user.getId(), activityDto);

        return CommentLikesMapper.toCommentLikeDto(saved);
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
