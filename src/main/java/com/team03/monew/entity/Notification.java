package com.team03.monew.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Notification extends BaseTimeEntity{

  @Builder
  public Notification(User user, String content, ResourceType relatedType, UUID relatedId) {
    this.user = user;
    this.content = content;
    this.relatedType = relatedType;
    this.relatedId = relatedId;
    this.checked = false;
  }


  @Id @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @Column(nullable = false)
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ResourceType relatedType;

  @Column(nullable = false)
  private UUID relatedId;

  @Column(nullable = false)
  private boolean checked = false;



  public enum ResourceType {
    INTEREST, COMMENT
  }

  // getter/setter
}
