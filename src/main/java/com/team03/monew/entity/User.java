package com.team03.monew.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;

@Entity
@Table(name = "users")
@Getter
public class User {
  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, length = 50)
  private String nickname;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private boolean deleted = false;

  // 연관 관계 생략 가능 (ex. 관심사, 댓글 등)

  // getter/setter
}
