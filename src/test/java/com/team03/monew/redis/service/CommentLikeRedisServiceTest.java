package com.team03.monew.redis.service;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.team03.monew.dto.comment.response.CommentLikeDto;
import com.team03.monew.entity.Comment;
import com.team03.monew.entity.CommentLike;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.entity.User;
import com.team03.monew.redis.Strategy;
import com.team03.monew.repository.CommentLikeRepository;
import com.team03.monew.repository.CommentRepository;
import com.team03.monew.repository.UserRepository;
import com.team03.monew.service.CommentServiceImpl;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 100명의 유저가 1~10번 댓글에 좋아요/취소 요청을 총 1000건 보냄
 * 병렬 처리(20개 스레드)를 통해 동시에 요청을 보낸 뒤
 * 총 처리 시간과 평균 응답 시간을 측정한다.
 */
@ExtendWith(MockitoExtension.class)
class CommentLikeRedisServiceTest {

    @Mock
    private User user;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Comment comment;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RedisService redisService;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private CommentLikeRedisService likeService;

    @InjectMocks
    private CommentServiceImpl commentService;

    enum Strategy { V1, V2, DB }

    private static final int USER_COUNT = 1000;
    private static final int COMMENT_COUNT = 100;
    private static final int TOTAL_REQUESTS = USER_COUNT * COMMENT_COUNT;

    @BeforeEach
    void setUpMocks() {
        for (long i = 1; i <= COMMENT_COUNT; i++) {
            NewsArticle mockNews = NewsArticle.builder()
                    .id(i)
                    .title("뉴스 제목")
                    .build();

            User user = User.builder()
                    .id(i)
                    .nickname("좋아요누룬사람")
                    .build();

            User commentAuthor = User.builder()
                    .id(i)
                    .nickname("글작성자")
                    .build();

            Comment comment = Comment.builder()
                    .id(i)
                    .content("테스트 댓글")
                    .user(commentAuthor)
                    .news(mockNews)
                    .build();

            lenient().when(commentRepository.findById(i)).thenReturn(Optional.of(comment));
            lenient().when(userRepository.findById(i)).thenReturn(Optional.of(user));
        }

        lenient().when(commentLikeRepository.save(Mockito.any(CommentLike.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

    }


    @Test
    void V1_댓글_좋아요_취소_반복_성능측정() throws InterruptedException {
        runToggleAndReadTest(Strategy.V1);
    }

    @Test
    void V2_댓글_좋아요_취소_반복_성능측정() throws InterruptedException {
        runToggleAndReadTest(Strategy.V2);
    }

    @Test
    void DB_댓글_좋아요_취소_반복_성능측정() throws InterruptedException {
        runToggleAndReadTest(Strategy.DB);
    }





    private void runToggleAndReadTest(Strategy strategy) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(TOTAL_REQUESTS * 3);

        long start = System.nanoTime();

        for (int user = 1; user <= USER_COUNT; user++) {
            long userId = user;
            for (int comment = 1; comment <= COMMENT_COUNT; comment++) {
                long commentId = comment;

                executor.submit(() -> {
                    try {
                        toggle(strategy, commentId, userId, 1); // 누름
                    } finally {
                        latch.countDown();
                    }
                });

                executor.submit(() -> {
                    try {
                        toggle(strategy, commentId, userId, 2); // 취소
                    } finally {
                        latch.countDown();
                    }
                });

                executor.submit(() -> {
                    try {
                        toggle(strategy, commentId, userId, 3); // 다시 누름
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await();
        long end = System.nanoTime();

        long totalMs = (end - start) / 1_000_000;
        double avg = totalMs / (double) (TOTAL_REQUESTS * 3);

        System.out.println("==== [" + strategy + "] 토글 + 조회 테스트 ====");
        System.out.printf("총 처리 시간: %dms%n", totalMs);
        System.out.printf("평균 응답 시간: %.3fms%n", avg);

        for (int commentId = 1; commentId <= COMMENT_COUNT; commentId++) {
            int likeCount = switch (strategy) {
                case V1 -> likeService.getLikeCount((long) commentId, com.team03.monew.redis.Strategy.V1);
                case V2 -> likeService.getLikeCount((long) commentId, com.team03.monew.redis.Strategy.V2);
                case DB -> commentService.getLikeCount((long) commentId);
            };
        }

        executor.shutdown();
    }

    private void toggle(Strategy strategy, Long commentId, Long userId, int step) {
        switch (strategy) {
            case DB -> {
                if (step == 1 || step == 3) {
                    CommentLikeDto commentLikeDto = commentService.commentLikes(commentId, userId);
                } else if (step == 2) {
                    commentService.cancelCommentLike(commentId, userId);
                }
            }
            case V1 -> {
                int current = likeService.getLikeCount(commentId, com.team03.monew.redis.Strategy.V1);
                if (current % 2 == 0) {
                    likeService.likeCommentV1(commentId, userId);
                } else {
                    likeService.deleteLikeData(commentId, userId, com.team03.monew.redis.Strategy.V1);
                }
            }
            case V2 -> {
                int current = likeService.getLikeCount(commentId, com.team03.monew.redis.Strategy.V2);
//                if (current % 2 == 0) {
//                    likeService.increaseLikeCountV2(commentId);
//                } else {
//                    likeService.decreaseLikeCountV2(commentId);
 //               }
            }
        }
    }
}