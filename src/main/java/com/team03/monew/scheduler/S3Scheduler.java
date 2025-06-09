package com.team03.monew.scheduler;

import com.team03.monew.service.S3LogUploader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Scheduler {
  private final S3LogUploader s3LogUploader;

  @Scheduled(cron = "0 */10 * * * *") // 10분마다
  public void uploadRecentLogs() {
    Path logPath = Paths.get("./logs/application.log"); // 또는 최근 롤링된 파일
    if (Files.exists(logPath)) {
      try {
        s3LogUploader.uploadLogFile(logPath);
      } catch (Exception e) {
        log.error("S3 로그 업로드 실패", e);
      }
    }
  }
}
