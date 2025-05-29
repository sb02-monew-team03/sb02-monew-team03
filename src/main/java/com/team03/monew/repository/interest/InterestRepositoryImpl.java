package com.team03.monew.repository.interest;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.dto.junhyeok.CursorPageResponseInterestDto;
import com.team03.monew.dto.junhyeok.InterestDto;
import com.team03.monew.entity.Interest;
import com.team03.monew.entity.QInterest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

// InterestRepositoryImpl.java
@RequiredArgsConstructor
public class InterestRepositoryImpl implements InterestRepositoryCustom {

  private final JPAQueryFactory queryFactory;

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
      default -> interest.name.asc(); // 기본 정렬
    };

    // 쿼리 실행
    List<Interest> results = queryFactory
        .selectFrom(interest)
        .where(condition)
        .orderBy(sort)
        .limit(limit + 1) // 커서 페이지 처리를 위해 +1
        .fetch();

    // 결과 변환
    List<InterestDto> content = results.stream()
        .limit(limit)
        .map(i -> new InterestDto(
            i.getId(),
            i.getName(),
            i.getKeywords(),
            i.getSubscriberCount(),
            false // 구독 여부 판단 로직 없음
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
