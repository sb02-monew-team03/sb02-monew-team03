package com.team03.monew.service;

import com.team03.monew.dto.comment.request.CommentLikeDto;
import com.team03.monew.dto.comment.request.CommentRegisterRequest;
import com.team03.monew.dto.comment.response.CommentDto;
import java.util.UUID;

public interface CommentService {

    public CommentDto registerComment(CommentRegisterRequest commentRegisterRequest);

    public CommentLikeDto commentLikes(UUID commentId, UUID userId);

    public void cancelCommentLike(UUID commentId, UUID userId);

    void softDeleteComment(UUID commentId, UUID userId);

    void hardDeleteComment(UUID commentId, UUID userId);


}
