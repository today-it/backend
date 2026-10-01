package com.todayit.place.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 장소의 기본 정보와 운영 상태를 나타냅니다. */
@Entity
@Table(name = "place")
public class Place {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "place_id", nullable = false)
  private int placeId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
  private BigDecimal latitude;

  @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
  private BigDecimal longitude;

  @Enumerated(EnumType.STRING)
  private Category category;

  @Column(name = "view_count", nullable = false)
  private int viewCount;

  @Column(name = "is_active", nullable = false)
  private boolean isActive;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = true)
  private LocalDateTime updatedAt;

  protected Place() {}
}
