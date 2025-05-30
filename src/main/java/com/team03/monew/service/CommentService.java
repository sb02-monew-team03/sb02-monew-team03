package com.team03.monew.service;

import com.team03.monew.dto.comment.request.CommentUpdateRequest;
import com.team03.monew.dto.comment.response.CommentLikeDto;
import com.team03.monew.dto.comment.request.CommentRegisterRequest;
import com.team03.monew.dto.comment.response.CommentDto;
import com.team03.monew.dto.comment.response.CursorPageResponseCommentDto;
import com.team03.monew.repository.OrderBy;
import com.team03.monew.repository.SortDirection;
import java.time.LocalDateTime;
import java.util.UUID;

public interface CommentService {

    CursorPageResponseCommentDto<CommentDto> commentCursorPage(
            UUID articleId,
            OrderBy orderBy,
            SortDirection direction,
            UUID cursor,
            LocalDateTime after,
            int limit,
            UUID requesterId
    );

    CommentDto registerComment(CommentRegisterRequest commentRegisterRequest);

    CommentLikeDto commentLikes(UUID commentId, UUID userId);

    void cancelCommentLike(UUID commentId, UUID userId);

    void softDeleteComment(UUID commentId, UUID userId);

    void hardDeleteComment(UUID commentId, UUID userId);

    CommentDto updateComment(UUID commentId, UUID userId, CommentUpdateRequest commentUpdateRequest);



}
