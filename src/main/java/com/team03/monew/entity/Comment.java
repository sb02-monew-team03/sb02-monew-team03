package com.team03.monew.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "comments")
@NoArgsConstructor
public class Comment extends BaseTimeEntity{

  @Id
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  private NewsArticle news;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private int likeCount = 0;

  @Column(nullable = false)
  private boolean deleted = false;

  @Builder
  public Comment(String uuId, NewsArticle news, User user, String content, int likeCount, boolean deleted) {
    this.id = UUID.randomUUID().toString();
    this.news = news;
    this.user = user;
    this.content = content;
    this.likeCount = likeCount;
    this.deleted = deleted;
  }


}