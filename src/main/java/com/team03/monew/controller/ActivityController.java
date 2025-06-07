package com.team03.monew.controller;

import com.team03.monew.dto.user.UserActivityDto;
import com.team03.monew.service.ActivityService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-activities")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserActivityDto> getUserActivity(
        @PathVariable(name = "userId") UUID userId
       // @RequestHeader(name = "Monew-Request-User-ID") UUID requesterId
    ) {
        log.info("여기가 문제인가? ");
        UserActivityDto response = activityService.getUserActivity(userId);
        return ResponseEntity.ok(response);
    }
}
