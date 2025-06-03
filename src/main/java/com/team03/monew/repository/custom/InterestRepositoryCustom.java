package com.team03.monew.repository.custom;

import com.team03.monew.dto.interest.CursorPageResponseInterestDto;
import java.time.LocalDateTime;

// InterestRepositoryCustom.java
public interface InterestRepositoryCustom {
  CursorPageResponseInterestDto searchInterests(
      Long userId,
      String keyword,
      String orderBy,
      String direction,
      String cursor,
      LocalDateTime after,
      int limit
  );
}
