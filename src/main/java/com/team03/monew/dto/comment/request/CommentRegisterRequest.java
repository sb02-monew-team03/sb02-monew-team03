package com.team03.monew.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRegisterRequest(
        @NotNull(message = "게시글 ID는 필수입니다.")
        Long articleId,

        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId,

        @NotBlank(message = "댓글 내용은 필수입니다.")
        String comment
) {}
