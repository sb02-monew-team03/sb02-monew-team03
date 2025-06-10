package com.team03.monew.dto.comment.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto(UUID id, UUID articledId, UUID userId, String userNickname, String content, int likeCount, Boolean likedByMe,
                         LocalDateTime createdAt) {

}
