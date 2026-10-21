package com.todayit.recommendation.rule;

import com.todayit.recommendation.model.RecommendationItem;
import com.todayit.recommendation.model.UserPreference;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

/** 추천 규칙 평가를 위한 Drools RuleUnitData 정의 클래스입니다. */
public class RecommendationRuleUnit implements RuleUnitData {

  private final DataStore<UserPreference> preferences;
  private final DataStore<RecommendationItem> items;

  /** 기본 DataSource를 사용하는 인스턴스를 생성합니다. */
  public RecommendationRuleUnit() {
    this(DataSource.createStore(), DataSource.createStore());
  }

  /**
   * 지정된 DataStore를 주입받는 인스턴스를 생성합니다.
   *
   * @param preferences 사용자 선호도 저장소
   * @param items 추천 아이템 저장소
   */
  public RecommendationRuleUnit(
      DataStore<UserPreference> preferences, DataStore<RecommendationItem> items) {
    this.preferences = preferences;
    this.items = items;
  }

  /**
   * 사용자 선호도 DataStore를 반환합니다.
   *
   * @return preferences DataStore
   */
  public DataStore<UserPreference> getPreferences() {
    return preferences;
  }

  /**
   * 추천 아이템 DataStore를 반환합니다.
   *
   * @return items DataStore
   */
  public DataStore<RecommendationItem> getItems() {
    return items;
  }
}
