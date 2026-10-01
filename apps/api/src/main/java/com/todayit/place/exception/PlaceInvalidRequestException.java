package com.todayit.place.exception;

/** 장소 기능의 요청값이 올바르지 않을 때 발생하는 예외입니다. */
public class PlaceInvalidRequestException extends PlaceException {

  /** 장소 요청값 오류 예외를 생성합니다. */
  public PlaceInvalidRequestException() {
    super(PlaceErrorCode.INVALID_PAGINATION);
  }
}
