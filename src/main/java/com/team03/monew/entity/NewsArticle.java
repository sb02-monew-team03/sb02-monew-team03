package com.team03.monew.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "news_articles")
public class NewsArticle {
  @Id @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private String source;

  @Column(nullable = false, unique = true)
  private String originalLink;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private LocalDateTime date;

  @Lob
  private String summary;

  @ManyToOne(fetch = FetchType.LAZY)
  private Interest interest;

  @Column(nullable = false)
  private int viewCount = 0;

  @Column(nullable = false)
  private boolean deleted = false;

  @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments = new ArrayList<>();

  // 연관관계 편의 메서드
  public void addComment(Comment comment) {
    comments.add(comment);
    comment.setNews(this);
  }

  public void removeComment(Comment comment) {
    comments.remove(comment);
    comment.setNews(null);
  }

  public void increaseViewCount() {
    this.viewCount++;
  }

  // 논리 삭제
  public void markAsDeleted() {
    this.deleted = true;
  }

  // 복구 시 사용할 생성자
  public NewsArticle(String source, String originalLink, String title,
      LocalDateTime date, String summary, Interest interest) {
    this.source = source;
    this.originalLink = originalLink;
    this.title = title;
    this.date = date;
    this.summary = summary;
    this.interest = interest;
    this.viewCount = 0;
    this.deleted = false;
  }

  // getter/setter
}
