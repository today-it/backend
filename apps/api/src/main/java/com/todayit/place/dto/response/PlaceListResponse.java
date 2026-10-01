package com.todayit.place.dto.response;

import com.todayit.place.service.model.PlaceListResult;
import java.util.List;

/**
 * 장소 목록 조회 API의 응답입니다.
 *
 * @param success 요청 성공 여부
 * @param data 장소 목록과 페이지 정보
 */
public record PlaceListResponse(boolean success, Data data) {

  /**
   * 장소 목록 결과를 API 응답으로 변환합니다.
   *
   * @param result 장소 목록 결과
   * @return 장소 목록 응답
   */
  public static PlaceListResponse from(PlaceListResult result) {
    return new PlaceListResponse(
        true,
        new Data(
            result.content().stream().map(PlaceResponse::from).toList(),
            result.page(),
            result.size(),
            result.totalElements(),
            result.totalPages()));
  }

  /**
   * 장소 목록과 페이지 정보입니다.
   *
   * @param content 장소 목록
   * @param page 현재 페이지 번호
   * @param size 페이지 크기
   * @param totalElements 전체 장소 수
   * @param totalPages 전체 페이지 수
   */
  public record Data(
      List<PlaceResponse> content, int page, int size, long totalElements, int totalPages) {}
}
