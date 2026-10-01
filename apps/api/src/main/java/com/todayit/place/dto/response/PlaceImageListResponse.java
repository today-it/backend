package com.todayit.place.dto.response;

import com.todayit.common.response.CommonResponse;
import com.todayit.place.service.model.PlaceImageListResult;
import com.todayit.place.service.model.PlaceImageResult;
import java.util.List;

/**
 * 장소 사진 목록 조회 API의 응답입니다.
 *
 * @param success 요청 성공 여부
 * @param data 장소 사진 목록과 페이지 정보
 */
public record PlaceImageListResponse(boolean success, Data data)
    implements CommonResponse<PlaceImageListResponse.Data> {

  /**
   * 장소 사진 목록 결과를 API 응답으로 변환합니다.
   *
   * @param result 장소 사진 목록 결과
   * @return 장소 사진 목록 응답
   */
  public static PlaceImageListResponse from(PlaceImageListResult result) {
    return new PlaceImageListResponse(
        true,
        new Data(
            result.content().stream().map(PlaceImageResponse::from).toList(),
            result.page(),
            result.size(),
            result.totalElements()));
  }

  /**
   * 장소 사진 목록과 페이지 정보입니다.
   *
   * @param content 장소 사진 목록
   * @param page 현재 페이지 번호
   * @param size 페이지 크기
   * @param totalElements 전체 사진 수
   */
  public record Data(List<PlaceImageResponse> content, int page, int size, long totalElements) {}

  /**
   * 장소 사진 응답입니다.
   *
   * @param placeImageId 장소 사진 식별자
   * @param imageUrl 장소 사진 URL
   */
  public record PlaceImageResponse(int placeImageId, String imageUrl) {

    /**
     * 장소 사진 결과를 API 응답으로 변환합니다.
     *
     * @param result 장소 사진 결과
     * @return 장소 사진 응답
     */
    public static PlaceImageResponse from(PlaceImageResult result) {
      return new PlaceImageResponse(result.placeImageId(), result.imageUrl());
    }
  }
}
