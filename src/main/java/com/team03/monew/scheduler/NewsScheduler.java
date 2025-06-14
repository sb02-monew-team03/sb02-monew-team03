package com.team03.monew.scheduler;

import com.team03.monew.service.NotificationService;
import com.team03.monew.service.news.NewsBackupService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsScheduler {

    private final JobLauncher jobLauncher;
    private final Job newsCollectJob;

    private final NotificationService notificationService;

    private final NewsBackupService newsBackupService;

    @Scheduled(cron = "0 */10 * * * *")
    public void runBatchJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
            .addLong("timestamp", System.currentTimeMillis()) // 중복 실행 방지용
            .toJobParameters();

        jobLauncher.run(newsCollectJob, params);
    }

    @Scheduled(cron = "0 10 0 * * *") // 매일 00:10에 실행
    public void runDailyBackup() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        newsBackupService.backupArticlesFor(yesterday);
    }

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시에 알림 정리
    public void deleteOldNotifications() {
        System.out.println("[Scheduler] 오래된 알림 삭제 시작");
        notificationService.deleteCheckedNotificationsOlderThanOneWeek();
        System.out.println("[Scheduler] 오래된 알림 삭제 완료");
    }
}

