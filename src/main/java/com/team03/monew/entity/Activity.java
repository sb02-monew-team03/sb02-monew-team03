package com.team03.monew.entity;


import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "activities")
public class Activity {
  @Id
  private UUID userId;

  @ElementCollection
  private List<Long> recentCommentIds = new ArrayList<>();

  @ElementCollection
  private List<Long> recentLikedCommentIds = new ArrayList<>();

  @ElementCollection
  private List<Long> recentViewedNewsIds = new ArrayList<>();

  @ElementCollection
  private List<Long> subscribedInterestIds = new ArrayList<>();

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  private User user;

  // getter/setter
}
