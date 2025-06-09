package com.team03.monew.controller;

import com.team03.monew.dto.user.UserActivityDto;
import com.team03.monew.service.activity.ActivityService;
import com.team03.monew.service.activity.ActivityServiceRouter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityServiceRouter activityServiceRouter;

    @GetMapping("/{userId}")
    public ResponseEntity<UserActivityDto> getUserActivity(
        @PathVariable UUID userId,
        @RequestHeader("MoNew-Request-User-ID") UUID requesterId
    ) {
        ActivityService activityService = activityServiceRouter.resolve();
        UserActivityDto response = activityService.getUserActivity(userId, requesterId);
        return ResponseEntity.ok(response);
    }
}
