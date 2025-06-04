package com.team03.monew.external;

import com.team03.monew.external.naver.NaverApiCollector;
import com.team03.monew.external.rss.RssCollector;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class NewsCollectTestController {

    private final RssCollector rssCollector;
    private final NaverApiCollector naverApiCollector;

    @GetMapping("/rss")
    public ResponseEntity<String> testRssCollect() {
        rssCollector.collectAll(); // 수동으로 실행
        return ResponseEntity.ok("수집 완료");
    }

    @GetMapping("/naver")
    public ResponseEntity<String> collectNaver() {
        naverApiCollector.collect();
        return ResponseEntity.ok("네이버 수집 완료");
    }

    @GetMapping("/all")
    public ResponseEntity<String> collectAll() {
        rssCollector.collectAll();
        naverApiCollector.collect();
        return ResponseEntity.ok("전체 수집 완료");
    }
}