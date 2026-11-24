package com.todayit.course.exception;

/** 이미 좋아요를 누른 코스에 다시 좋아요를 요청했을 때 발생하는 예외입니다. */
public class CourseAlreadyLikedException extends CourseException {

  /** 이미 좋아요를 누른 코스 오류를 생성합니다. */
  public CourseAlreadyLikedException() {
    super(CourseErrorCode.ALREADY_LIKED);
  }
}
