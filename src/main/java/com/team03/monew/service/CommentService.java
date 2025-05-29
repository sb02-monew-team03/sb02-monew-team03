package com.team03.monew.service;

import com.team03.monew.dto.comment.request.CommentUpdateRequest;
import com.team03.monew.dto.comment.response.CommentLikeDto;
import com.team03.monew.dto.comment.request.CommentRegisterRequest;
import com.team03.monew.dto.comment.response.CommentDto;
import java.util.UUID;

public interface CommentService {

    CommentDto registerComment(CommentRegisterRequest commentRegisterRequest);

    CommentLikeDto commentLikes(UUID commentId, UUID userId);

    void cancelCommentLike(UUID commentId, UUID userId);

    void softDeleteComment(UUID commentId, UUID userId);

    void hardDeleteComment(UUID commentId, UUID userId);

    CommentDto updateComment(UUID commentId, UUID userId, CommentUpdateRequest commentUpdateRequest);



}
