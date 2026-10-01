package com.todayit.place.controller;

import com.todayit.place.dto.response.PlaceListResponse;
import com.todayit.place.dto.response.PlaceLocationResponse;
import com.todayit.place.exception.PlaceInvalidRequestException;
import com.todayit.place.service.PlaceService;
import com.todayit.place.service.model.PlaceSort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 장소 관련 HTTP 요청을 처리하는 Controller입니다. */
@RestController
@RequestMapping("/api/v1/places")
public class PlaceController {

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 20;

  private final PlaceService placeService;

  /**
   * 장소 기능을 처리할 서비스를 받습니다.
   *
   * @param placeService 장소 Service
   */
  public PlaceController(PlaceService placeService) {
    this.placeService = placeService;
  }

  /**
   * 활성화되고 삭제되지 않은 장소 목록을 조회합니다.
   *
   * @param page 페이지 번호
   * @param size 페이지 크기
   * @param sort 정렬 기준
   * @return 장소 목록과 페이지 정보
   */
  @GetMapping
  public ResponseEntity<PlaceListResponse> findPlaces(
      @RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
      @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size,
      @RequestParam(defaultValue = "LATEST") PlaceSort sort) {
    validatePagination(page, size);
    return ResponseEntity.ok(PlaceListResponse.from(placeService.findPlaces(page, size, sort)));
  }

  /**
   * 장소의 지도 표시 정보를 조회합니다.
   *
   * @param placeId 장소 식별자
   * @return 장소 위치 응답
   */
  @GetMapping("/{placeId}/location")
  public ResponseEntity<PlaceLocationResponse> findPlaceLocation(@PathVariable int placeId) {
    return ResponseEntity.ok(PlaceLocationResponse.from(placeService.findPlaceLocation(placeId)));
  }

  /**
   * 장소 목록 조회의 페이지 요청값을 검증합니다.
   *
   * @param page 0 이상이어야 하는 페이지 번호
   * @param size 1 이상이어야 하는 페이지 크기
   * @throws PlaceInvalidRequestException page가 0보다 작거나 size가 1보다 작을 때
   */
  private void validatePagination(int page, int size) {
    if (page < 0) {
      throw new PlaceInvalidRequestException();
    }
    if (size <= 0) {
      throw new PlaceInvalidRequestException();
    }
  }
}
