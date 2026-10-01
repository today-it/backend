package com.todayit.place.service.model;

import java.math.BigDecimal;

/**
 * 장소 지도 조회 결과입니다.
 *
 * @param placeId 장소 식별자
 * @param latitude 위도
 * @param longitude 경도
 * @param address 주소
 */
public record PlaceLocationResult(
    int placeId, BigDecimal latitude, BigDecimal longitude, String address) {}
