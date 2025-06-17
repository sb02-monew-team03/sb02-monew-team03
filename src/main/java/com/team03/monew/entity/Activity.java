package com.team03.monew.entity;


import com.team03.monew.dto.comment.response.CommentActivityDto;
import com.team03.monew.dto.comment.response.CommentLikeActivityDto;
import com.team03.monew.dto.newsArticle.response.ArticleViewDto;
import com.team03.monew.dto.subscription.SubscriptionDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "activities")
public class Activity {

    @Id
    private UUID userId;

    private String email;
    private String nickname;
    private LocalDateTime createdAt;

    private List<SubscriptionDto> subscriptions;
    private List<CommentActivityDto> comments;
    private List<CommentLikeActivityDto> commentLikes;
    private List<ArticleViewDto> articleViews;

}
