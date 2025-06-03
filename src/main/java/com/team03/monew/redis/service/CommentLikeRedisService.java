package com.team03.monew.redis.service;

import com.team03.monew.redis.Strategy;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentLikeRedisService {

    private final RedisService redisService;

    private static final String PREFIX_LIKE_COUNT_V1 = "comment:like-count:v1:";
    private static final String PREFIX_LIKE_COUNT_V2 = "comment:like-count:v2:";
    private static final String PREFIX_USER_LIKE = "comment:like:v:";

    // V1: get + set 방식
    public void likeCommentV1(Long commentId, Long userId) {
        String userKey = getUserKey(commentId, userId);
        String countKey = getV1Key(commentId);

        System.out.println("➡️ [V1] 좋아요 요청: commentId = " + commentId + ", userId = " + userId);
        System.out.println(" - 생성될 유저 Key: " + userKey);
        System.out.println(" - 좋아요 수 Key: " + countKey);

        if (!redisService.hasKey(userKey)) {
            System.out.println("🆕 유저가 처음 누름 → 좋아요 처리 진행");

            redisService.set(userKey, "1");
            int current = getLikeCount(commentId, Strategy.V1);
            System.out.println("현재 좋아요 수: " + current + " → " + (current + 1));

            redisService.set(countKey, current + 1);
            System.out.println("✅ 좋아요 수 증가 완료");
        } else {
            System.out.println("⚠️ 이미 누른 유저 → 좋아요 처리 안함");
        }
    }


    // V2: increment 방식
    public void likeCommentV2(Long commentId, Long userId) {
        String userKey = getUserKey(commentId, userId);
        String countKey = getV2Key(commentId);

        System.out.println("➡️ [V2] 좋아요 요청: commentId = " + commentId + ", userId = " + userId);
        System.out.println(" - 유저 Key: " + userKey);
        System.out.println(" - 좋아요 수 Key: " + countKey);

        if (!redisService.hasKey(userKey)) {
            System.out.println("🆕 유저가 처음 누름 → 좋아요 처리 진행");

            redisService.set(userKey, "1");
            long result = redisService.increment(countKey);

            System.out.println("✅ 좋아요 수 증가 완료 → 현재 count = " + result);
        } else {
            System.out.println("⚠️ 이미 누른 유저 → 좋아요 처리 안함");
        }
    }

    // 총 좋아요 수 조회
    public int getLikeCount(Long commentId, Strategy strategy) {
        String key;

        if (strategy == Strategy.V1) {
            key = getV1Key(commentId);
        } else if (strategy == Strategy.V2) {
            key = getV2Key(commentId);
        } else {
            throw new IllegalArgumentException("Invalid strategy");
        }

        System.out.println("📥 좋아요 수 조회 요청 → strategy = " + strategy + ", commentId = " + commentId);
        System.out.println("🔑 조회할 Redis Key: " + key);

        Object val = redisService.get(key);

        if (val == null) {
            System.out.println("⛔ Redis에서 값 없음 → 기본값 0 반환");
            return 0;
        }

        int result = Integer.parseInt(val.toString());
        System.out.println("✅ Redis 조회 결과 = " + val + " → 파싱 후 = " + result);
        return result;
    }

    // 초기화
    public void initLikeCount(Long commentId, int count, Strategy strategy) {
        String key;
        if (strategy == Strategy.V1) {
            key = getV1Key(commentId);
        } else if (strategy == Strategy.V2) {
            key = getV2Key(commentId);
        } else {
            throw new IllegalArgumentException("Invalid strategy");
        }

        redisService.set(key, count, 24, TimeUnit.HOURS);
    }

    // 삭제 (테스트용)
    public void deleteLikeData(Long commentId, Long userId, Strategy strategy) {
        redisService.delete(getUserKey(commentId, userId));

        String key;
        if (strategy == Strategy.V1) {
            key = getV1Key(commentId);
        } else if (strategy == Strategy.V2) {
            key = getV2Key(commentId);
        } else {
            return;
        }

        redisService.delete(key);
    }

    // ====== Key 생성 ======

    private String getV1Key(Long commentId) {
        return PREFIX_LIKE_COUNT_V1 + commentId;
    }

    private String getV2Key(Long commentId) {
        return PREFIX_LIKE_COUNT_V2 + commentId;
    }

    private String getUserKey(Long commentId, Long userId) {
        return PREFIX_USER_LIKE + commentId + ":" + userId;
    }

}
