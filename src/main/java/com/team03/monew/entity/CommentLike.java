package com.team03.monew.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class CommentLike extends BaseTimeEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private boolean likedByMe = false;

    @Builder
    public CommentLike(Comment comment, User user, boolean likedByMe) {
        this.id = UUID.randomUUID();
        this.comment = comment;
        this.user = user;
        this.likedByMe = likedByMe;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

}
