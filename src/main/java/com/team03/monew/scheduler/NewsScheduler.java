package com.team03.monew.scheduler;

import com.team03.monew.collector.RssCollector;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsScheduler {

    // 각 콜렉터로 정보를 받아올 예정
//    private final NaverApiCollector naverApiCollector;
    private final RssCollector rssCollector;

    @Scheduled(cron = "0 0 * * * *") // 매 시 정각마다 실행
    public void collectNews() {
//        naverApiCollector.collect();
        rssCollector.collect();
    }
}

