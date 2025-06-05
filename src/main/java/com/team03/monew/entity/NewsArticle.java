package com.team03.monew.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
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

  @Column(columnDefinition = "text", nullable = false, unique = true)
  private String originalLink;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private LocalDateTime date;

  private String summary;

  @ManyToMany
  @JoinTable(
      name = "news_article_interest",
      joinColumns = @JoinColumn(name = "article_id"),
      inverseJoinColumns = @JoinColumn(name = "interest_id")
  )
  private List<Interest> interests = new ArrayList<>();

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

  @Builder
  public NewsArticle(UUID id, String source, String originalLink, String title,
      LocalDateTime date, String summary,
      List<Interest> interests, int viewCount, boolean deleted) {
    this.id = id;
    this.source = source;
    this.originalLink = originalLink;
    this.title = title;
    this.date = date;
    this.summary = summary;
    this.interests = interests;
    this.viewCount = viewCount;
    this.deleted = deleted;
  }

  // getter/setter
}
