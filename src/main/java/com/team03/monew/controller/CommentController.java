package com.team03.monew.controller;

import com.team03.monew.dto.comment.request.CommentLikeDto;
import com.team03.monew.dto.comment.request.CommentRegisterRequest;
import com.team03.monew.dto.comment.response.CommentDto;
import com.team03.monew.service.CommentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentRegisterRequest commentRegisterRequest) {
        CommentDto registerComment = commentService.registerComment(commentRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerComment);
    }

    @PostMapping("/{commentId}/comment-likes")
    public ResponseEntity<CommentLikeDto> addCommentLike(@PathVariable UUID commentId,@RequestParam UUID userId) {
        CommentLikeDto commentLikeDto = commentService.commentLikes(commentId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(commentLikeDto);
    }

    @DeleteMapping("/{commentId}/comment-likes")
    public ResponseEntity<Void> cancelCommentLike(@PathVariable UUID commentId, @RequestParam UUID userId) {
        commentService.cancelCommentLike(commentId, userId);
        return ResponseEntity.noContent().build();
    }

}
