package com.todayit.place.dto.response;

import com.todayit.place.service.model.PlaceLocationResult;
import java.math.BigDecimal;

/**
 * 장소 지도 조회 API 응답입니다.
 *
 * @param success 요청 성공 여부
 * @param data 장소 위치 정보
 */
public record PlaceLocationResponse(boolean success, Data data) {

  /**
   * 장소 위치 조회 결과를 API 응답으로 변환합니다.
   *
   * @param result 장소 위치 조회 결과
   * @return 장소 위치 응답
   */
  public static PlaceLocationResponse from(PlaceLocationResult result) {
    return new PlaceLocationResponse(
        true, new Data(result.placeId(), result.latitude(), result.longitude(), result.address()));
  }

  /**
   * 장소 위치 정보입니다.
   *
   * @param placeId 장소 식별자
   * @param latitude 위도
   * @param longitude 경도
   * @param address 주소
   */
  public record Data(int placeId, BigDecimal latitude, BigDecimal longitude, String address) {}
}
