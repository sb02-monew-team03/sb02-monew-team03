package com.team03.monew.controller;

import com.team03.monew.dto.interest.CursorPageResponseInterestDto;
import com.team03.monew.dto.interest.InterestDto;
import com.team03.monew.dto.interest.InterestRegisterRequest;
import com.team03.monew.dto.interest.InterestUpdateRequest;
import com.team03.monew.dto.subscription.SubscriptionDto;
import com.team03.monew.service.InterestService;
import com.team03.monew.service.NotificationService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

  private final InterestService interestService;
  private final NotificationService notificationService;

  @PostMapping
  public ResponseEntity<InterestDto> registerInterest(@RequestBody @Valid InterestRegisterRequest request) {
    InterestDto result = interestService.registerInterest(request);
    return ResponseEntity.ok(result);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Void> updateInterestKeywords(
      @PathVariable(name = "id") UUID id,
      @RequestBody @Valid InterestUpdateRequest request
  ) {
    interestService.updateKeywords(id, request.keywords());
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseInterestDto> searchInterests(
          @RequestHeader(name = "Monew-Request-User-ID") UUID userId,
          @RequestParam(name = "keyword", required = false) String keyword,
          @RequestParam(name = "orderBy") String orderBy,
          @RequestParam(name = "direction") String direction,
          @RequestParam(name = "cursor", required = false) String cursor,
          @RequestParam(name = "after", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
          @RequestParam(name = "limit") int limit
  ) {
    CursorPageResponseInterestDto result = interestService.searchInterests(
            userId, keyword, orderBy, direction, cursor, after, limit
    ).orElseThrow();
    return ResponseEntity.ok(result);
  }

  @PostMapping("/{interestId}/subscriptions")
  public ResponseEntity<SubscriptionDto> subscribe(
          @PathVariable(name = "interestId") UUID interestId,
          @RequestHeader(name = "Monew-Request-User-ID") UUID userId
  ) {
    SubscriptionDto dto = interestService.subscribe(interestId, userId);
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping("/{interestId}/subscriptions")
  public ResponseEntity<Void> unsubscribe(
          @PathVariable(name = "interestId") UUID interestId,
          @RequestHeader(name = "Monew-Request-User-ID") UUID userId
  ) {
    interestService.unsubscribe(interestId, userId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{interestId}")
  public ResponseEntity<Void> deleteInterest(
          @PathVariable(name = "interestId") UUID interestId
  ) {
    interestService.delete(interestId);
    return ResponseEntity.noContent().build();
  }

}
