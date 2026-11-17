package com.todayit.course.exception;

import com.todayit.common.exception.ErrorCode;
import com.todayit.common.exception.ErrorStatus;

/** 코스 생성에서 사용하는 오류 코드입니다. */
public enum CourseErrorCode implements ErrorCode {
  /** 코스명이 없거나 허용 길이를 벗어난 경우입니다. */
  INVALID_TITLE("INVALID_COURSE_TITLE", "코스명은 1자 이상 100자 이하이어야 합니다."),

  /** 지역 코드가 없거나 형식이 올바르지 않은 경우입니다. */
  INVALID_REGION("INVALID_COURSE_REGION", "지역은 시도 2자리 또는 시도·시군구 5자리 코드이어야 합니다."),

  /** 콘셉트 개수나 값, 중복 여부가 올바르지 않은 경우입니다. */
  INVALID_THEMES("INVALID_COURSE_THEMES", "콘셉트는 중복 없이 1개 이상 2개 이하로 입력해야 합니다."),

  /** 등록되지 않은 콘셉트를 요청한 경우입니다. */
  UNSUPPORTED_THEME("UNSUPPORTED_COURSE_THEME", "지원하지 않는 콘셉트가 포함되어 있습니다."),

  /** 시작 시각을 입력하지 않은 경우입니다. */
  START_AT_REQUIRED("COURSE_START_AT_REQUIRED", "시작 시각은 필수입니다."),

  /** 이동수단을 입력하지 않은 경우입니다. */
  TRANSPORT_REQUIRED("COURSE_TRANSPORT_REQUIRED", "이동수단은 필수입니다."),

  /** 종료 시각과 가용시간을 모두 입력하거나 모두 생략한 경우입니다. */
  INVALID_TIME_CONDITION("INVALID_COURSE_TIME_CONDITION", "종료 시각과 가용시간 중 하나만 입력해야 합니다."),

  /** 종료 시각이 시작 시각보다 늦지 않은 경우입니다. */
  INVALID_END_AT("INVALID_COURSE_END_AT", "종료 시각은 시작 시각 이후이어야 합니다."),

  /** 가용시간이 양의 정수가 아닌 경우입니다. */
  INVALID_DURATION("INVALID_COURSE_DURATION", "가용시간은 1분 이상이어야 합니다.");

  private final String code;
  private final String message;

  CourseErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }

  /** {@inheritDoc} */
  @Override
  public String code() {
    return code;
  }

  /** {@inheritDoc} */
  @Override
  public String message() {
    return message;
  }

  /** {@inheritDoc} */
  @Override
  public ErrorStatus status() {
    return ErrorStatus.BAD_REQUEST;
  }
}
