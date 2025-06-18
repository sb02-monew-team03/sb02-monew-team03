package com.team03.monew.service.activity;

import com.team03.monew.dto.user.UserActivityDto;
import com.team03.monew.entity.Activity;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.ActivityRepository;
import com.team03.monew.repository.UserRepository;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityServiceMongoImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Override
    public UserActivityDto getUserActivity(UUID userId) {
        Activity activity = activityRepository.findById(userId)
            .orElseGet(() -> {
                User user = userRepository.findByIdAndDeletedFalse(userId)
                    .orElseThrow(() -> {
                        ErrorDetail detail = new ErrorDetail("UUID", "userId", userId.toString());
                        return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.USER);
                    });

                return Activity.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .createdAt(user.getCreatedAt())
                    .subscriptions(new ArrayList<>())
                    .comments(new ArrayList<>())
                    .commentLikes(new ArrayList<>())
                    .articleViews(new ArrayList<>())
                    .build();
            });

        return toDto(activity);
    }

    private UserActivityDto toDto(Activity activity) {
        return new UserActivityDto(
            activity.getUserId(),
            activity.getEmail(),
            activity.getNickname(),
            activity.getCreatedAt(),
            activity.getSubscriptions(),
            activity.getComments(),
            activity.getCommentLikes(),
            activity.getArticleViews()
        );
    }
}
