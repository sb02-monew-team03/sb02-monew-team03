package com.team03.monew.service.activity;

import com.team03.monew.dto.comment.response.CommentActivityDto;
import com.team03.monew.dto.comment.response.CommentLikeActivityDto;
import com.team03.monew.dto.newsArticle.mapper.ArticleViewMapper;
import com.team03.monew.dto.newsArticle.response.ArticleViewDto;
import com.team03.monew.dto.subscription.SubscriptionDto;
import com.team03.monew.dto.user.UserActivityDto;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.ArticleViewRepository;
import com.team03.monew.repository.CommentLikeRepository;
import com.team03.monew.repository.CommentRepository;
import com.team03.monew.repository.SubscriptionRepository;
import com.team03.monew.repository.UserRepository;
import com.team03.monew.dto.subscription.mapper.SubsciptionMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityServiceLazyImpl implements ActivityService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ArticleViewRepository articleViewRepository;

    @Override
    public UserActivityDto getUserActivity(UUID userId, UUID requesterId) {
        if (!userId.equals(requesterId)) {
            ErrorDetail detail = new ErrorDetail("USER", "userId", requesterId.toString());
            throw new CustomException(ErrorCode.FORBIDDEN, detail, ExceptionType.USER);
        }

        // 1. 사용자 정보 조회
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(() -> {
            ErrorDetail detail = new ErrorDetail("UUID", "userId", userId.toString());
            return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.USER);
        });

        // 2. 구독 중인 관심사 조회 (구독 최신순 정렬)
        List<SubscriptionDto> subscriptions = subscriptionRepository
            .findByUserOrderByCreatedAtDesc(user)
            .stream()
            .map(SubsciptionMapper::from)
            .toList();

        // 3. 최근 작성한 댓글 최대 10건 조회 (최신순)
        List<CommentActivityDto> comments = commentRepository
            .findTop10ByUserOrderByCreatedAtDesc(user)
            .stream()
            .map(CommentActivityDto::from)
            .toList();

        // 4. 최근 좋아요를 누른 댓글 최대 10건 조회 (최신순)
        List<CommentLikeActivityDto> commentLikes = commentLikeRepository
            .findTop10ByUserOrderByCreatedAtDesc(user)
            .stream()
            .map(CommentLikeActivityDto::from)
            .toList();

        // 5. 최근 본 뉴스 기사 최대 10건 조회 (최신순)
        List<ArticleViewDto> articleViews = articleViewRepository
            .findTop10ByUserOrderByCreatedAtDesc(user)
            .stream()
            .map(ArticleViewMapper::toDto)
            .toList();

        return new UserActivityDto(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getCreatedAt(),
            subscriptions,
            comments,
            commentLikes,
            articleViews
        );

    }
}
