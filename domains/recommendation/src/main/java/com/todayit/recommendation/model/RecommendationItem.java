package com.todayit.recommendation.model;

/** 룰 평가 대상 및 결과를 담는 추천 아이템 모델입니다. */
public class RecommendationItem {

  private final String itemId;
  private final String category;
  private int score;

  /**
   * 추천 아이템을 생성합니다. 기본 점수는 0점입니다.
   *
   * @param itemId 아이템 식별자
   * @param category 아이템 카테고리
   */
  public RecommendationItem(String itemId, String category) {
    this.itemId = itemId;
    this.category = category;
    this.score = 0;
  }

  /**
   * 아이템 식별자를 반환합니다.
   *
   * @return 아이템 식별자
   */
  public String getItemId() {
    return itemId;
  }

  /**
   * 카테고리를 반환합니다.
   *
   * @return 카테고리
   */
  public String getCategory() {
    return category;
  }

  /**
   * 계산된 추천 점수를 반환합니다.
   *
   * @return 추천 점수
   */
  public int getScore() {
    return score;
  }

  /**
   * 추천 점수를 가산합니다.
   *
   * @param point 추가할 점수
   */
  public void addScore(int point) {
    this.score += point;
  }
}
