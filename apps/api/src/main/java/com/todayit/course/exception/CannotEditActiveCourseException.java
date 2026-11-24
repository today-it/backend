package com.todayit.course.exception;

/** 확정(ACTIVE) 상태의 코스는 생성 조건을 수정할 수 없을 때 발생하는 예외입니다. */
public class CannotEditActiveCourseException extends CourseException {

  /** 확정 코스 생성 조건 수정 불가 예외를 생성합니다. */
  public CannotEditActiveCourseException() {
    super(CourseErrorCode.CANNOT_EDIT_ACTIVE_COURSE);
  }
}
