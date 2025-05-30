package com.team03.monew.service;

import com.team03.monew.dto.interest.CursorPageResponseInterestDto;
import com.team03.monew.repository.InterestRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestService {

  private final InterestRepository interestRepository;

  public Optional<CursorPageResponseInterestDto>  searchInterests(
      UUID userId,
      String keyword,
      String orderBy,
      String direction,
      String cursor,
      LocalDateTime after,
      int limit
  ) {
    return Optional.of(interestRepository.searchInterests(userId, keyword, orderBy, direction, cursor, after, limit)) ;
  }
}
