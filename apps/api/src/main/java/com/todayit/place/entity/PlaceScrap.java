package com.todayit.place.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** 회원이 스크랩한 장소 정보를 나타냅니다. */
@Entity
@Table(name = "place_scrap")
public class PlaceScrap {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "place_scrap_id", nullable = false)
  private int placeScrapId;

  @Column(name = "member_id", nullable = false, length = 36)
  private String memberId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "place_id", nullable = false)
  private Place place;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at", nullable = false)
  private LocalDateTime deletedAt;

  protected PlaceScrap() {}

  /**
   * 스크랩 식별자를 반환합니다.
   *
   * @return 스크랩 식별자
   */
  public int getPlaceScrapId() {
    return placeScrapId;
  }

  /**
   * 회원 식별자를 반환합니다.
   *
   * @return 회원 식별자
   */
  public String getMemberId() {
    return memberId;
  }

  /**
   * 삭제 여부를 반환합니다.
   *
   * @return 삭제 여부
   */
  public boolean isDeleted() {
    return isDeleted;
  }

  /**
   * 생성 시각을 반환합니다.
   *
   * @return 생성 시각
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * 수정 시각을 반환합니다.
   *
   * @return 수정 시각
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   * 삭제 시각을 반환합니다.
   *
   * @return 삭제 시각
   */
  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }
}
