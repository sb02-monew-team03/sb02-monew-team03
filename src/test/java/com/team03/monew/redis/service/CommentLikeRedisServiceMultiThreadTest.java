package com.team03.monew.redis.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.team03.monew.redis.Strategy;
import com.team03.monew.service.CommentServiceImpl;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CommentLikeRedisServiceMultiThreadTest {

    @Autowired
    RedisService redisService;

    @Autowired
    CommentLikeRedisService likeService;

    @Autowired
    private CommentServiceImpl commentService;


    private final Strategy strategyV2 = Strategy.V2;
    private final Strategy strategyV1 = Strategy.V1;
    private final Strategy strategyDB = Strategy.DB;

    private static final int USER_COUNT = 5;
    private static final int COMMENT_COUNT = 3;
    private static final int TOTAL_REQUESTS = USER_COUNT * COMMENT_COUNT;
    private static final String PREFIX_LIKE_COUNT_V1 = "comment:like-count:v1:";
    private static final String PREFIX_LIKE_COUNT_V2 = "comment:like-count:v2:";

    @Test
    void V1_100명_10댓글_좋아요_성능측정() throws InterruptedException {
        runConcurrentLikeTest(strategyV1);
    }

//    @Test
//    void V2_100명_10댓글_좋아요_성능측정() throws InterruptedException {
//        runConcurrentLikeTest(strategyV2);
//    }
//
//    @Test
//    void DB_100명_10개_댓글_좋아요_성능측정() throws InterruptedException {
//        runConcurrentLikeTest(strategyDB);
//    }

    private void runConcurrentLikeTest(Strategy strategy) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(TOTAL_REQUESTS);

        long start = System.nanoTime();

        for (int user = 1; user <= USER_COUNT; user++) {
            for (int comment = 1; comment <= COMMENT_COUNT; comment++) {
                final long userId = user;
                final long commentId = comment;

                executor.submit(() -> {
                    try {
                        boolean logThis = ThreadLocalRandom.current().nextInt(10) < 10;

                        switch (strategy) {
                            case DB -> {
                                if (logThis) {
                                    System.out.printf("[DB] user: %d → comment: %d%n", userId, commentId);
                                }
                                commentService.commentLikes(commentId, userId);
                            }
                            case V1 -> {
                                try {
                                    if (logThis) {
                                        System.out.printf("[V1] user: %d → comment: %d%n", userId, commentId);
                                    }
                                    likeService.likeCommentV1(commentId, userId);
                                    String userKey = "comment:like:v:" + commentId + ":" + userId;
                                    Object userValue = redisService.get(userKey);
                                    System.out.println("유저 좋아요 여부 key = " + userKey + ", value = " + userValue);
                                } catch (Exception e) {
                                    System.err.printf(" [V1 예외] user: %d, comment: %d -> %s%n", userId, commentId,
                                            e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                            case V2 -> {
                                try {
                                    if (logThis) {
                                        System.out.printf("[V2] user: %d → comment: %d%n", userId, commentId);
                                    }
                                    likeService.likeCommentV2(commentId, userId);
                                    String userKey = "comment:like:v:" + commentId + ":" + userId;
                                    Object userValue = redisService.get(userKey);
                                    System.out.println("유저 좋아요 여부 key = " + userKey + ", value = " + userValue);
                                } catch (Exception e) {
                                    System.err.printf(" [V2 예외] user: %d, comment: %d -> %s%n", userId, commentId,
                                            e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await();
        long end = System.nanoTime();

        long totalMs = (end - start) / 1_000_000;
        double avg = totalMs / (double) TOTAL_REQUESTS;

        System.out.println("==== [" + strategy + "] 성능 측정 결과 ====");
        System.out.printf("총 처리 시간: %dms%n", totalMs);
        System.out.printf("평균 응답 시간: %.3fms%n", avg);
        System.out.printf("[%s] 모든 요청 완료 (총 %d건)%n", strategy, TOTAL_REQUESTS);

        // 정확성 검증
        int expectedTotal = USER_COUNT * COMMENT_COUNT;
        int actualTotal = 0;

        for (int i = 1; i <= COMMENT_COUNT; i++) {
            actualTotal += likeService.getLikeCount((long) i, strategy);
        }

        System.out.println(likeService.getLikeCount(1L, strategy));
        System.out.println(likeService.getLikeCount(10L, strategy));
        System.out.println(likeService.getLikeCount(400L, strategy));

        System.out.println("==== 정확도 검증 결과 ====");
        System.out.printf("예상 총합: %d, 실제 총합: %d%n", expectedTotal, actualTotal);
        if (expectedTotal == actualTotal) {
            System.out.println(" 정확성 PASS");
        } else {
            double accuracyRate = (actualTotal / (double) expectedTotal) * 100;
            System.out.printf(" 정확성 FAIL (정확도: %.2f%%)%n", accuracyRate);
        }

        executor.shutdown();
    }

}