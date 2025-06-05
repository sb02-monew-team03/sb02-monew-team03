package com.team03.monew.controller;

import com.team03.monew.dto.user.UserActivityDto;
import com.team03.monew.service.ActivityService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<UserActivityDto> getUserActivity(@PathVariable UUID userId) {
        UserActivityDto response = activityService.getUserActivity(userId);
        return ResponseEntity.ok(response);
    }
}
