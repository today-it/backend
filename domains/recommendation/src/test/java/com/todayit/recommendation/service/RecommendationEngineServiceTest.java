package com.todayit.recommendation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.todayit.recommendation.model.RecommendationItem;
import com.todayit.recommendation.model.UserPreference;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendationEngineServiceTest {

  @Test
  @DisplayName("사용자 선호 카테고리와 일치하는 아이템은 룰에 의해 가중치 점수가 부여된다")
  void shouldAddScoreWhenCategoryMatchesUserPreference() {
    // given
    RecommendationEngineService engine = new RecommendationEngineService();
    UserPreference preference = new UserPreference("user-1", "AI");

    RecommendationItem matchingItem = new RecommendationItem("item-1", "AI");
    RecommendationItem nonMatchingItem = new RecommendationItem("item-2", "WEB");
    List<RecommendationItem> items = List.of(matchingItem, nonMatchingItem);

    // when
    List<RecommendationItem> result = engine.evaluate(preference, items);

    // then
    assertEquals(2, result.size());
    assertEquals(10, matchingItem.getScore());
    assertEquals(0, nonMatchingItem.getScore());
  }
}
