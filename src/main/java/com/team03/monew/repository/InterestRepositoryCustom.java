package com.team03.monew.repository;

import com.team03.monew.dto.CursorPageResponseInterestDto;
import com.team03.monew.dto.InterestDto;
import java.time.LocalDateTime;
import java.util.UUID;

// InterestRepositoryCustom.java
public interface InterestRepositoryCustom {
  CursorPageResponseInterestDto searchInterests(
      UUID userId,
      String keyword,
      String orderBy,
      String direction,
      String cursor,
      LocalDateTime after,
      int limit
  );
}
