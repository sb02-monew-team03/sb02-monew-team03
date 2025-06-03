package com.team03.monew.controller;

import com.team03.monew.dto.interest.CursorPageResponseInterestDto;
import com.team03.monew.dto.interest.InterestDto;
import com.team03.monew.dto.interest.InterestRegisterRequest;
import com.team03.monew.dto.interest.InterestUpdateRequest;
import com.team03.monew.service.InterestService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

  @PostMapping
  public ResponseEntity<InterestDto> registerInterest(@RequestBody @Valid InterestRegisterRequest request) {
    InterestDto result = interestService.registerInterest(request);
    return ResponseEntity.ok(result);
  }

  @PatchMapping("/{id}/keywords")
  public ResponseEntity<Void> updateInterestKeywords(
      @PathVariable Long id,
      @RequestBody @Valid InterestUpdateRequest request
  ) {
    interestService.updateKeywords(id, request.keywords());
    return ResponseEntity.noContent().build();
  }


  @GetMapping
  public ResponseEntity<CursorPageResponseInterestDto> searchInterests(
      @RequestHeader("Monew-Request-User-ID") Long userId,
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
