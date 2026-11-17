package com.todayit.course.service.model;

import com.todayit.course.entity.Course;
import com.todayit.course.entity.CourseStatus;
import java.time.LocalDateTime;

/**
 * 코스 생성 결과입니다.
 *
 * @param courseId 코스 식별자
 * @param title 코스명
 * @param status DRAFT 상태
 * @param courseSummary 코스 설명, 직접 생성 시 null
 * @param createdAt 서버 생성 시각
 */
public record CreateCourseResult(
    int courseId,
    String title,
    CourseStatus status,
    String courseSummary,
    LocalDateTime createdAt) {

  /**
   * 코스 Entity의 생성 정보를 서비스 결과로 변환합니다.
   *
   * @param course 저장한 코스
   * @return 코스 생성 결과
   */
  public static CreateCourseResult from(Course course) {
    var snapshot = course.getSnapshot();
    return new CreateCourseResult(
        snapshot.courseId(),
        snapshot.title(),
        course.getStatus(),
        snapshot.courseSummary(),
        course.getCreatedAt());
  }
}
