package com.team03.monew.controller.testController;

import com.team03.monew.dto.newsArticle.response.ArticleRestoreResultDto;
import com.team03.monew.service.NewsRestoreService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/restore")
public class RestoreTestController {

    private final NewsRestoreService newsRestoreService;

    @PostMapping
    public ResponseEntity<List<ArticleRestoreResultDto>> restore(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<ArticleRestoreResultDto> results = newsRestoreService.restore(from, to);
        return ResponseEntity.ok(results);
    }
}
