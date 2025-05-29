package com.team03.monew.service;

import com.team03.monew.dto.comment.request.CommentRegisterRequest;
import com.team03.monew.dto.comment.response.CommentDto;

public interface CommentService {

    public CommentDto registerComment(CommentRegisterRequest commentRegisterRequest);
}
