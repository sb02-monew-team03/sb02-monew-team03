package com.team03.monew.service.activity;

import com.team03.monew.dto.comment.response.CommentActivityDto;
import com.team03.monew.dto.comment.response.CommentLikeActivityDto;
import com.team03.monew.dto.newsArticle.response.ArticleViewDto;
import com.team03.monew.dto.subscription.SubscriptionDto;
import com.team03.monew.entity.Activity;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.ActivityRepository;
import com.team03.monew.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityDocumentUpdater {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public void addSubscription(UUID userId, SubscriptionDto subscriptionDto) {
        Activity activity = getOrCreateActivity(userId);

        List<SubscriptionDto> subscriptions = activity.getSubscriptions();

        boolean alreadyExists = subscriptions.stream()
            .anyMatch(s -> s.interestId().equals(subscriptionDto.interestId()));

        if (!alreadyExists) {
            subscriptions.add(subscriptionDto);
        }

        activityRepository.save(activity);
    }

    public void addRecentComment(UUID userId, CommentActivityDto commentDto) {
        Activity activity = getOrCreateActivity(userId);

        List<CommentActivityDto> comments = activity.getComments();
        comments.add(0, commentDto);

        if (comments.size() > 10) {
            comments.remove(10);
        }

        activityRepository.save(activity);
    }

    public void addRecentCommentLike(UUID userId, CommentLikeActivityDto likeDto) {
        Activity activity = getOrCreateActivity(userId);

        List<CommentLikeActivityDto> likes = activity.getCommentLikes();
        likes.add(0, likeDto);

        if (likes.size() > 10) {
            likes.remove(10);
        }

        activityRepository.save(activity);
    }

    public void addRecentArticleView(UUID userId, ArticleViewDto viewDto) {
        Activity activity = getOrCreateActivity(userId);

        List<ArticleViewDto> views = activity.getArticleViews();
        views.add(0, viewDto);

        if (views.size() > 10) {
            views.remove(10);
        }

        activityRepository.save(activity);
    }

    private Activity getOrCreateActivity(UUID userId) {

        return activityRepository.findById(userId)
            .orElseGet(() -> {
                User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(() -> {
                    ErrorDetail detail = new ErrorDetail("UUID", "userId", userId.toString());
                    return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail,
                        ExceptionType.USER);
                });

                Activity newActivity = Activity.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .createdAt(user.getCreatedAt())
                    .comments(new ArrayList<>())
                    .commentLikes(new ArrayList<>())
                    .articleViews(new ArrayList<>())
                    .subscriptions(new ArrayList<>())
                    .build();

                // MongoDB에 저장 후 반환
                return activityRepository.save(newActivity);
            });
    }
}
