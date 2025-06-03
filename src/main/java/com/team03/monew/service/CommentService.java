package com.team03.monew.service;

import com.team03.monew.dto.comment.request.CommentUpdateRequest;
import com.team03.monew.dto.comment.response.CommentLikeDto;
import com.team03.monew.dto.comment.request.CommentRegisterRequest;
import com.team03.monew.dto.comment.response.CommentDto;
import com.team03.monew.dto.comment.response.CursorPageResponseCommentDto;
import com.team03.monew.util.OrderBy;
import com.team03.monew.util.SortDirection;
import java.time.LocalDateTime;

public interface CommentService {

    CursorPageResponseCommentDto<CommentDto> commentCursorPage(
            Long articleId,
            OrderBy orderBy,
            SortDirection direction,
            Long cursor,
            LocalDateTime after,
            int limit,
            Long requesterId
    );

    CommentDto registerComment(CommentRegisterRequest commentRegisterRequest);

    CommentLikeDto commentLikes(Long commentId, Long userId);

    void cancelCommentLike(Long commentId, Long userId);

    void softDeleteComment(Long commentId, Long userId);

    void hardDeleteComment(Long commentId, Long userId);

    CommentDto updateComment(Long commentId, Long userId, CommentUpdateRequest commentUpdateRequest);



}
