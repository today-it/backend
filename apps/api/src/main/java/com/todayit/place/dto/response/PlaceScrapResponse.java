package com.todayit.place.dto.response;

import com.todayit.place.service.model.PlaceScrapResult;

/**
 * 장소 스크랩 API 응답입니다.
 *
 * @param success 요청 성공 여부
 * @param data 장소 스크랩 결과
 */
public record PlaceScrapResponse(boolean success, Data data) {

  /**
   * 장소 스크랩 결과를 API 응답으로 변환합니다.
   *
   * @param result 장소 스크랩 결과
   * @return 장소 스크랩 응답
   */
  public static PlaceScrapResponse from(PlaceScrapResult result) {
    return new PlaceScrapResponse(
        true, new Data(result.placeId(), result.scrapped(), result.scrapCount()));
  }

  /**
   * 장소 스크랩 결과 데이터입니다.
   *
   * @param placeId 장소 식별자
   * @param scrapped 현재 회원의 스크랩 여부
   * @param scrapCount 장소의 활성 스크랩 수
   */
  public record Data(int placeId, boolean scrapped, long scrapCount) {}
}
