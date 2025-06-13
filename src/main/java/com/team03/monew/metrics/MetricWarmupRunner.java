package com.team03.monew.metrics;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class MetricWarmupRunner implements ApplicationRunner {

    private final BatchJobMetrics metrics;

    public MetricWarmupRunner(BatchJobMetrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public void run(ApplicationArguments args) {
        metrics.recordSuccess("newsCollectJob");
        metrics.recordFailure("newsCollectJob");
    }
}
