package com.team03.monew.controller;

import com.team03.monew.collector.RssCollector;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class RssTestController {

    private final RssCollector rssCollector;

    @GetMapping("/rss")
    public ResponseEntity<String> testRssCollect() {
        rssCollector.collect(); // 수동으로 실행
        return ResponseEntity.ok("수집 완료");
    }
}