package com.team03.monew.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchJobMetrics {

    private final MeterRegistry meterRegistry;

    // job 성공 기록
    public void recordSuccess(String jobName) {
        meterRegistry.counter("batch.job.success.count", "job", jobName).increment();
    }

    // job 실패 기록
    public void recordFailure(String jobName) {
        meterRegistry.counter("batch.job.failure.count", "job", jobName).increment();
    }

    // Timer
    public void recordDuration(String jobName, Duration duration) {
        Timer.builder("batch.job.duration")
            .description("Batch Job Execution Time")
            .tag("job", jobName)
            .register(meterRegistry)
            .record(duration);
    }
}
