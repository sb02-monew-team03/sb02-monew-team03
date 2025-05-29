package com.team03.monew.dto.comment.response;

import com.team03.monew.entity.NewsArticle;
import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDto(UUID id, UUID articledId, UUID userId, String userNickName, String content, int likeCount, Boolean likedByMe,
                         LocalDateTime createdAt) {

}
