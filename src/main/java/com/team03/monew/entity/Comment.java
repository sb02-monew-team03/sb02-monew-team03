package com.team03.monew.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "comments")
@NoArgsConstructor
public class Comment extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private NewsArticle news;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;  // 누가 썼는지

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private int likeCount = 0;


  private Boolean deleted = false;


  @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CommentLike> commentLikes = new ArrayList<>();

  @Builder
  public Comment(Long id, NewsArticle news, User user, String content) {
    this.id = id;
    this.news = news;
    this.user = user;
    this.content = content;
  }

  public void addCommentLike(CommentLike commentLike) {
    if (!commentLikes.contains(commentLike)) {
      commentLikes.add(commentLike);
      commentLike.setComment(this);
    }
  }

  public void removeCommentLike(CommentLike commentLike) {
    if (commentLikes.remove(commentLike)) {
      commentLike.setComment(null);
    }
  }

  public void setNews(NewsArticle news) {
    this.news = news;
  }

  public void increaseLikeCount() {
    this.likeCount += 1;
  }

  public void decreaseLikeCount() {
    if (this.likeCount > 0) {
      this.likeCount -= 1;
    }
  }

  public void markAsDeleted() {
    this.deleted = true;
  }

  public void updateContent(String content) {
    if (content != null) {
      this.content = content;
    }
  }

  public boolean isDeleted() {
    return deleted;
  }
}