package com.team03.monew.redis.service;


import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CommentLikeRedisService {

    private final RedisService redisService;
    private static final String PREFIX = "comment:like-count:";


    // 좋아요 수 증가 (원자적)
    public void increaseLikeCountV2(Long commentId) {
        redisService.increment(getKey(commentId));
    }

    // 좋아요 수 감소 (원자적)
    public void decreaseLikeCountV2(Long commentId) {
        redisService.decrement(getKey(commentId));
    }

    public void increaseLikeCountV1(Long commentId) {
        String key = getKey(commentId);
        int currentCount = getLikeCount(commentId);
        int updatedCount = currentCount + 1;
        redisService.set(key, updatedCount);
    }

    public void decreaseLikeCountV1(Long commentId) {
        String key = getKey(commentId);
        int currentCount = getLikeCount(commentId);
        int updatedCount = 0;

        if (currentCount > 0) {
            updatedCount = currentCount - 1;
        }

        redisService.set(key, updatedCount);
    }

    public int getLikeCount(Long commentId) {
        String key = getKey(commentId);
        Object value = redisService.get(key);

        if (value == null) {
            return 0;
        }

        return Integer.parseInt(value.toString());
    }

    public void initLikeCount(Long commentId, int count) {
        String key = getKey(commentId);
        redisService.set(key, count, 24, TimeUnit.HOURS);
    }

    public void deleteLikeCount(Long commentId) {
        redisService.delete(getKey(commentId));
    }

    private String getKey(Long commentId) {
        return PREFIX + commentId.toString();
    }
}

