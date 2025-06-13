package com.team03.monew.batch.config;

import com.team03.monew.batch.processor.NewsArticleProcessor;
import com.team03.monew.batch.reader.NaverNewsReader;
import com.team03.monew.batch.reader.RssNewsReader;
import com.team03.monew.batch.writer.NewsArticleWriter;
import com.team03.monew.dto.newsArticle.request.NewsArticleRequestDto;
import com.team03.monew.metrics.JobMetricListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class NewsCollectJobConfig {
    // 전체 job 구성
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final NaverNewsReader naverNewsReader;
    private final RssNewsReader rssNewsReader;
    private final NewsArticleWriter newsArticleWriter;
    private final NewsArticleProcessor newsArticleProcessor;
    private final JobMetricListener jobMetricListener;

    @Bean
    public Job newsCollectJob() {
        return new JobBuilder("newsCollectJob", jobRepository)
            .listener(jobMetricListener) // 메트릭 리스너
            .start(naverStep())
            .next(rssStep())
            .build();
    }

    @Bean
    public Step naverStep() {
        return new StepBuilder("naverStep", jobRepository)
            .<NewsArticleRequestDto, NewsArticleRequestDto>chunk(10, transactionManager)
            .reader(naverNewsReader)
            .processor(newsArticleProcessor)
            .writer(newsArticleWriter)
            .build();
    }

    @Bean
    public Step rssStep() {
        return new StepBuilder("rssStep", jobRepository)
            .<NewsArticleRequestDto, NewsArticleRequestDto>chunk(10, transactionManager)
            .reader(rssNewsReader)
            .processor(newsArticleProcessor)
            .writer(newsArticleWriter)
            .build();
    }
}
