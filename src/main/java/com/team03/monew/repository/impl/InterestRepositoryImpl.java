package com.team03.monew.repository.impl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.dto.interest.CursorPageResponseInterestDto;
import com.team03.monew.dto.interest.InterestDto;
import com.team03.monew.entity.Interest;
import com.team03.monew.entity.QInterest;
import com.team03.monew.entity.Subscription;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.UserRepository;
import com.team03.monew.repository.custom.InterestRepositoryCustom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// InterestRepositoryImpl.java
@RequiredArgsConstructor
@Repository
public class InterestRepositoryImpl implements InterestRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final UserRepository userRepository;

  @Override
  public CursorPageResponseInterestDto searchInterests(UUID userId, String keyword, String orderBy, String direction,
      String cursor, LocalDateTime after, int limit) {
    QInterest interest = QInterest.interest;

    // 검색 조건
    BooleanBuilder condition = new BooleanBuilder();
    if (keyword != null && !keyword.isBlank()) {
      condition.and(
          interest.name.containsIgnoreCase(keyword)
              .or(interest.keywords.any().containsIgnoreCase(keyword))
      );
    }

    if (after != null) {
      condition.and(interest.createdAt.gt(after));
    }

    // 정렬 조건 설정
    OrderSpecifier<?> sort = switch (orderBy) {

      case "name" -> direction.equalsIgnoreCase("DESC") ? interest.name.desc() : interest.name.asc();
      case "subscriberCount" -> direction.equalsIgnoreCase("DESC") ? interest.subscriberCount.desc() : interest.subscriberCount.asc();
      default -> throw new CustomException(
          ErrorCode.INVALID_INPUT_VALUE,
          new ErrorDetail("name | subscriberCount", "orderBy", orderBy),
          ExceptionType.INTEREST
      ); // 기본 정렬
    };

    // 쿼리 실행
    List<Interest> results = queryFactory
        .selectFrom(interest)
        .where(condition)
        .orderBy(sort)
        .limit(limit + 1) // 커서 페이지 처리를 위해 +1
        .fetch();

    // 결과 변환
    User login_user = userRepository.findById(userId).orElseThrow(
        () -> new CustomException(
        ErrorCode.RESOURCE_NOT_FOUND,
        new ErrorDetail("User", "userId", "user"),
        ExceptionType.INTEREST
    ));

    List<InterestDto> content = results.stream()
        .limit(limit)
        .map(i -> new InterestDto(
            i.getId(),
            i.getName(),
            i.getKeywords(),
            i.getSubscriberCount(),
            login_user.hasSubscribed(i)
        ))
        .toList();

    boolean hasNext = results.size() > limit;
    String nextCursor = hasNext ? content.get(content.size() - 1).id().toString() : null;
    LocalDateTime nextAfter = hasNext ? results.get(limit - 1).getCreatedAt() : null;

    return new CursorPageResponseInterestDto(
        content,
        nextCursor,
        nextAfter,
        content.size(),
        results.size(),
        hasNext
    );
  }
}
