package com.team03.monew.dto.comment.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CommentDto(String id, String articledId, String userId, String userNickName, String content, int likeCount, Boolean likedByMe,
                         LocalDateTime createdAt) {
}
