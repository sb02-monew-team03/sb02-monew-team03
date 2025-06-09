package com.team03.monew.service.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityServiceRouter {

    // 기본 전략을 yml 파일에서 가져옴
    @Value("${custom.activity-service-strategy}")
    private String strategy;

    private final ActivityServiceLazyImpl lazyService;
    private final ActivityServiceFetchJoinImpl fetchService;
    private final ActivityServiceMongoImpl mongoService;

    public ActivityService resolve() {
        return switch (strategy.toLowerCase()) {
            case "fetch" -> fetchService;
            case "mongo" -> mongoService;
            default -> lazyService;
        };
    }
}
