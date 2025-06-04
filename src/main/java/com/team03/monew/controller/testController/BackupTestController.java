package com.team03.monew.controller.testController;

import com.team03.monew.service.NewsBackupService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/backup")
public class BackupTestController {

    private final NewsBackupService newsBackupService;

    @PostMapping
    public ResponseEntity<String> backup(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        newsBackupService.backupArticlesFor(date);
        return ResponseEntity.ok("백업 요청 완료: " + date);
    }
}
