package com.team03.monew.service;

import com.team03.monew.dto.notification.CursorPageResponseNotificationDto;
import com.team03.monew.dto.notification.NotificationDto;
import com.team03.monew.entity.Interest;
import com.team03.monew.entity.Notification;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.InterestRepository;
import com.team03.monew.repository.NotificationRepository;
import com.team03.monew.repository.SubscriptionRepository;
import com.team03.monew.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final SubscriptionRepository subscriptionRepository;


  @Transactional
  public CursorPageResponseNotificationDto getNotifications(UUID userId, UUID cursor, LocalDateTime after, int limit) {
    Pageable pageable = PageRequest.of(0, limit);
    List<Notification> notifications = notificationRepository.findByCursorAndAfter(userId, cursor, after, pageable);

    List<NotificationDto> content = notifications.stream()
            .map(NotificationDto::from)
            .toList();

    boolean hasNext = content.size() == limit;
    String nextCursor = hasNext ? content.get(content.size() - 1).id().toString() : null;
    LocalDateTime nextAfter = hasNext ? content.get(content.size() - 1).createdAt() : null;
    long totalElements = notificationRepository.countByUserId(userId);

    return new CursorPageResponseNotificationDto(
            content, nextCursor, nextAfter, limit, totalElements, hasNext
    );
  }


  @Transactional
  public void confirmAll(UUID userId) {
    notificationRepository.updateAllCheckedByUserId(userId);
  }

  @Transactional
  public void confirm(UUID notificationId, UUID userId) {
    Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
        .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
    notification.setChecked(true);
    notification.setUpdatedAt(LocalDateTime.now());
  }

  @Transactional
  public void notifyInterestNews(Interest interest, int articleCount) {
    List<User> subscribers = subscriptionRepository.findAllByInterest(interest);

    for (User user : subscribers) {
      String content = String.format("[%s]와 관련된 기사가 %d건 등록되었습니다.", interest.getName(),
          articleCount);

      Notification notification = Notification.builder()
          .user(user)
          .content(content)
          .relatedType(Notification.ResourceType.INTEREST)
          .relatedId(interest.getId())
          .build();

      notificationRepository.save(notification);
    }
  }

  public void notifyCommentLiked(UUID receiverUserId, UUID commentId) {
    User receiver = userRepository.findById(receiverUserId)
        .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND,new ErrorDetail(
            "User","receiverUserId","user"
        ), ExceptionType.INTEREST));


    String content = String.format("[%s]님이 나의 댓글을 좋아합니다.", receiver.getNickname());

    Notification notification = Notification.builder()
        .user(receiver)
        .content(content)
        .relatedType(Notification.ResourceType.COMMENT)
        .relatedId(commentId)
        .build();

    notificationRepository.save(notification);
  }

  @Transactional
  public void deleteCheckedNotificationsOlderThanOneWeek() {
    LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
    notificationRepository.deleteByCheckedIsTrueAndUpdatedAtBefore(oneWeekAgo);
  }
}
