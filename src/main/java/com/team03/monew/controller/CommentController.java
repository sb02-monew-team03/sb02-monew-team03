package com.team03.monew.controller;

import com.team03.monew.dto.comment.request.CommentUpdateRequest;
import com.team03.monew.dto.comment.response.CommentLikeDto;
import com.team03.monew.dto.comment.request.CommentRegisterRequest;
import com.team03.monew.dto.comment.response.CommentDto;
import com.team03.monew.dto.comment.response.CursorPageResponseCommentDto;
import com.team03.monew.entity.Comment;
import com.team03.monew.repository.OrderBy;
import com.team03.monew.repository.SortDirection;
import com.team03.monew.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping()
    public CursorPageResponseCommentDto<CommentDto> getCommentsWithCursorPaging(
            @RequestParam("articleId") UUID articleId,
            @RequestParam("orderBy") OrderBy orderBy,
            @RequestParam("direction") SortDirection direction,
            @RequestParam(value = "cursor", required = false) UUID cursor,
            @RequestParam(value = "after", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
            @RequestParam(value = "limit", defaultValue = "50")
            @Min(1) @Max(100) int limit,
            @RequestHeader("userId") UUID userId
    ) {
        return commentService.commentCursorPage(articleId, orderBy, direction, cursor, after, limit, userId);
    }


    @PostMapping
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentRegisterRequest commentRegisterRequest) {
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

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> softDeleteComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId
    ) {
        commentService.softDeleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}/hard")
    public ResponseEntity<Void> hardDeleteComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId
    ) {
        commentService.hardDeleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        CommentDto updated = commentService.updateComment(commentId, userId, request);
        return ResponseEntity.ok(updated);
    }


}
