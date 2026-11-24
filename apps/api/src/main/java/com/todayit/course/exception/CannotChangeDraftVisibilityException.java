package com.todayit.course.exception;

/** 임시저장(DRAFT) 상태의 코스는 공개 범위를 변경할 수 없을 때 발생하는 예외입니다. */
public class CannotChangeDraftVisibilityException extends CourseException {

  /** 임시저장 코스 공개 범위 변경 불가 예외를 생성합니다. */
  public CannotChangeDraftVisibilityException() {
    super(CourseErrorCode.CANNOT_CHANGE_DRAFT_VISIBILITY);
  }
}
