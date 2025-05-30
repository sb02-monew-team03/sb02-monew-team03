package com.team03.monew.controller;

import com.team03.monew.dto.interest.CursorPageResponseInterestDto;
import com.team03.monew.service.InterestService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

  private final InterestService interestService;

  @GetMapping
  public ResponseEntity<CursorPageResponseInterestDto> searchInterests(
      @RequestHeader("Monew-Request-User-ID") UUID userId,
      @RequestParam(required = false) String keyword,
      @RequestParam String orderBy,
      @RequestParam String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
      @RequestParam int limit
  ) {
    CursorPageResponseInterestDto result = interestService.searchInterests(
        userId, keyword, orderBy, direction, cursor, after, limit
    ).orElseThrow();
    return ResponseEntity.ok(result);
  }
}
