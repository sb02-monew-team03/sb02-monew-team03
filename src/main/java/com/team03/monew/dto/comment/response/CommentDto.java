package com.team03.monew.dto.comment.response;

import java.time.LocalDateTime;


public record CommentDto(Long id, Long articledId, Long userId, String userNickName, String content, int likeCount, Boolean likedByMe,
                         LocalDateTime createdAt) {

}
