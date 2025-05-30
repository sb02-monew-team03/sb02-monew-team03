package com.team03.monew.redis.controller;

import com.team03.monew.redis.service.CommentLikeRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/comments/redis")
@RequiredArgsConstructor
public class CommentLikeRedisController {

    private final CommentLikeRedisService commentLikeRedisService;

    @GetMapping("/{commentId}/like-count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Long commentId) {
        int likeCount = commentLikeRedisService.getLikeCount(commentId);
        return ResponseEntity.ok(likeCount);
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> increaseLike(@PathVariable Long commentId) {
        commentLikeRedisService.increaseLikeCount(commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<Void> decreaseLike(@PathVariable Long commentId) {
        commentLikeRedisService.decreaseLikeCount(commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}/like-cache")
    public ResponseEntity<Void> deleteCache(@PathVariable Long commentId) {
        commentLikeRedisService.deleteLikeCount(commentId);
        return ResponseEntity.noContent().build();
    }
}
