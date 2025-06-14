package com.team03.monew.metrics;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobMetricListener implements JobExecutionListener {

    private final BatchJobMetrics batchJobMetrics;

    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();

        LocalDateTime start = jobExecution.getStartTime();
        LocalDateTime end = jobExecution.getEndTime();

        // 시스템에 맞는 오프셋
        ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();

        Duration duration = Duration.between(start.toInstant(zoneOffset), end.toInstant(zoneOffset));
        // 실행 시간 측정
        batchJobMetrics.recordDuration(jobName, duration);

        // 성공/실패 기록
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            batchJobMetrics.recordSuccess(jobName);
            log.info("[배치 실패] jobName: {}, duration: {}ms", jobName, duration);
        } else {
            batchJobMetrics.recordFailure(jobName);
            log.info("[배치 성공] jobName: {}, duration: {}ms", jobName, duration);
        }
    }
}
