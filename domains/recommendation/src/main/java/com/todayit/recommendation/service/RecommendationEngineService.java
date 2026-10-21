package com.todayit.recommendation.service;

import com.todayit.recommendation.model.RecommendationItem;
import com.todayit.recommendation.model.UserPreference;
import com.todayit.recommendation.rule.RecommendationRuleUnit;
import java.util.List;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;

/** Drools Rule Unit 기반으로 추천 점수를 계산하는 서비스 구현체입니다. */
public class RecommendationEngineService {

  /** 추천 엔진 서비스를 생성합니다. */
  public RecommendationEngineService() {}

  /**
   * 사용자 선호도와 후보 아이템 목록을 받아 Drools 룰을 실행하고 점수를 반영합니다.
   *
   * @param preference 사용자 선호도
   * @param items 평가할 추천 아이템 목록
   * @return 룰 평가가 완료된 추천 아이템 목록
   */
  public List<RecommendationItem> evaluate(
      UserPreference preference, List<RecommendationItem> items) {
    RecommendationRuleUnit unitData = new RecommendationRuleUnit();

    if (preference != null) {
      unitData.getPreferences().add(preference);
    }
    if (items != null) {
      for (RecommendationItem item : items) {
        unitData.getItems().add(item);
      }
    }

    try (RuleUnitInstance<RecommendationRuleUnit> instance =
        RuleUnitProvider.get().createRuleUnitInstance(unitData)) {
      instance.fire();
    }

    return items;
  }
}
