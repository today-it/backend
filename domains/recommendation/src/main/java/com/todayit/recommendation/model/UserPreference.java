package com.todayit.recommendation.model;

/** 룰 평가에 사용되는 사용자 선호도 모델입니다. */
public class UserPreference {

  private final String userId;
  private final String preferredCategory;

  /**
   * 사용자 선호도를 생성합니다.
   *
   * @param userId 사용자 식별자
   * @param preferredCategory 선호 카테고리
   */
  public UserPreference(String userId, String preferredCategory) {
    this.userId = userId;
    this.preferredCategory = preferredCategory;
  }

  /**
   * 사용자 식별자를 반환합니다.
   *
   * @return 사용자 식별자
   */
  public String getUserId() {
    return userId;
  }

  /**
   * 선호 카테고리를 반환합니다.
   *
   * @return 선호 카테고리
   */
  public String getPreferredCategory() {
    return preferredCategory;
  }
}
