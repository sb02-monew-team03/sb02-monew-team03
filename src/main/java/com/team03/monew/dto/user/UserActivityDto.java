package com.team03.monew.dto.user;

import com.team03.monew.dto.comment.response.CommentActivityDto;
import com.team03.monew.dto.comment.response.CommentLikeActivityDto;
import com.team03.monew.dto.newsArticle.response.ArticleViewDto;
import com.team03.monew.dto.subscription.SubscriptionDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserActivityDto(
    UUID id,
    String email,
    String nickname,
    LocalDateTime createdAt,
    List<SubscriptionDto> subscriptions,
    List<CommentActivityDto> comments,
    List<CommentLikeActivityDto> commentLikes,
    List<ArticleViewDto> articleViews

) {

}
