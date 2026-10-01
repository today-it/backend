package com.todayit.place.service.model;

import java.util.List;

/**
 * 장소 사진 목록 조회 결과입니다.
 *
 * @param content 장소 사진 목록
 * @param page 현재 페이지 번호
 * @param size 페이지 크기
 * @param totalElements 전체 사진 수
 */
public record PlaceImageListResult(
    List<PlaceImageResult> content, int page, int size, long totalElements) {}
