package com.team03.monew.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "news_articles")
public class NewsArticle {
  @Id @GeneratedValue
  private Long id;

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

  // getter/setter
}
