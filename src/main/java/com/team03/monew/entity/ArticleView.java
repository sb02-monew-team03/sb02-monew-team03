package com.team03.monew.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "article_views", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"article_id", "userId"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleView {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private NewsArticle article;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ArticleView(NewsArticle article, Long userId) {
        this.article = article;
        this.userId = userId;
    }
}

