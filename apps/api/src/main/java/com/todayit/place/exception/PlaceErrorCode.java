package com.todayit.place.exception;

import com.todayit.common.exception.ErrorCode;
import com.todayit.common.exception.ErrorStatus;

/** 장소 기능에서 사용하는 오류 코드입니다. */
public enum PlaceErrorCode implements ErrorCode {
  /** 장소 목록의 페이지 번호나 크기가 올바르지 않은 경우입니다. */
  INVALID_PAGINATION(
      "PLACE_INVALID_PAGINATION", "장소 목록 페이지 요청값이 올바르지 않습니다.", ErrorStatus.BAD_REQUEST);

  /** 클라이언트에 전달할 오류 코드입니다. */
  private final String code;

  /** 클라이언트에 전달할 오류 메시지입니다. */
  private final String message;

  /** 오류를 변환할 HTTP 상태입니다. */
  private final ErrorStatus status;

  /**
   * 장소 오류 코드를 생성합니다.
   *
   * @param code 클라이언트에 전달할 오류 코드
   * @param message 클라이언트에 전달할 오류 메시지
   * @param status 오류를 변환할 HTTP 상태
   */
  PlaceErrorCode(String code, String message, ErrorStatus status) {
    this.code = code;
    this.message = message;
    this.status = status;
  }

  /**
   * 클라이언트에 전달할 오류 코드를 반환합니다.
   *
   * @return 오류 코드
   */
  @Override
  public String code() {
    return code;
  }

  /**
   * 클라이언트에 전달할 오류 메시지를 반환합니다.
   *
   * @return 오류 메시지
   */
  @Override
  public String message() {
    return message;
  }

  /**
   * 오류를 변환할 HTTP 상태를 반환합니다.
   *
   * @return 오류 상태
   */
  @Override
  public ErrorStatus status() {
    return status;
  }
}
