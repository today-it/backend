package com.todayit.course.service.model;

import com.todayit.course.entity.Course;

/**
 * 코스 복사 결과입니다.
 *
 * @param courseId 복사되어 새로 생성된 코스 식별자
 */
public record CopyCourseResult(int courseId) {

  /**
   * 저장된 코스 엔티티로부터 복사 결과를 생성합니다.
   *
   * @param course 저장한 코스
   * @return 코스 복사 결과
   */
  public static CopyCourseResult from(Course course) {
    return new CopyCourseResult(course.getCourseId());
  }
}
