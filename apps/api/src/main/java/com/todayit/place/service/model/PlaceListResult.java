package com.todayit.place.service.model;

import java.util.List;

/**
 * 장소 목록 조회 결과입니다.
 *
 * @param content 장소 목록
 * @param page 현재 페이지 번호
 * @param size 페이지 크기
 * @param totalElements 전체 장소 수
 * @param totalPages 전체 페이지 수
 */
public record PlaceListResult(
    List<PlaceResult> content, int page, int size, long totalElements, int totalPages) {}
