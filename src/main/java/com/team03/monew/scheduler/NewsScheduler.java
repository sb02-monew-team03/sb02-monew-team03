package com.team03.monew.scheduler;

import com.team03.monew.external.naver.NaverApiCollector;
import com.team03.monew.external.rss.RssCollector;
import com.team03.monew.service.NotificationService;
import com.team03.monew.service.news.NewsBackupService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsScheduler {

    // 각 콜렉터를 통해 정보를 받아옴(api, rss 구분)
    private final NaverApiCollector naverApiCollector;
    private final RssCollector rssCollector;
    private final NotificationService notificationService;

    private final NewsBackupService newsBackupService;

    @Scheduled(cron = "0 */1 * * * *")
    public void collectNews() {
        System.out.println("[Scheduler] 수집 시작됨");

        naverApiCollector.collect();
        rssCollector.collectAll();
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

