package com.todayit.common.response;

/**
 * 성공 API 응답의 공통 형식입니다.
 *
 * @param <T> 응답 데이터 타입
 */
public interface CommonResponse<T> {

  /**
   * 요청 성공 여부를 반환합니다.
   *
   * @return 요청 성공 여부
   */
  boolean success();

  /**
   * 응답 데이터를 반환합니다.
   *
   * @return 응답 데이터
   */
  T data();
}
