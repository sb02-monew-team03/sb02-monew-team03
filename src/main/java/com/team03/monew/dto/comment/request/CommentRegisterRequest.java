package com.team03.monew.dto.comment.request;

import java.util.UUID;

public record CommentRegisterRequest(UUID articleId, UUID userId, String comment) {
}
