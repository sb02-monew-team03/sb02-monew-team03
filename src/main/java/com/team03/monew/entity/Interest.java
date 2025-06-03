package com.team03.monew.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "interests")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Interest {
  @Id @GeneratedValue
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @ElementCollection
  private List<String> keywords = new ArrayList<>();

  @Column(nullable = false)
  private int subscriberCount = 0;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();
  // getter/setter
}