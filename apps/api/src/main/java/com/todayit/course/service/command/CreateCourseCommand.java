package com.todayit.course.service.command;

import com.todayit.course.entity.CourseTransport;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 코스 생성 조건입니다.
 *
 * @param title 코스명
 * @param sidoCode 시·도 법정동 코드 2자리
 * @param sigunguCode 시·군·구 법정동 코드 3자리
 * @param themes 콘셉트 이름 목록
 * @param startAt 방문 시작 시각
 * @param endAt 방문 종료 시각
 * @param transport 이동수단
 */
public record CreateCourseCommand(
    String title,
    String sidoCode,
    String sigunguCode,
    List<String> themes,
    LocalDateTime startAt,
    LocalDateTime endAt,
    CourseTransport transport) {

  /**
   * 콘셉트 목록을 방어적으로 복사합니다.
   *
   * @param title 코스명
   * @param sidoCode 시·도 법정동 코드 2자리
   * @param sigunguCode 시·군·구 법정동 코드 3자리
   * @param themes 콘셉트 이름 목록
   * @param startAt 방문 시작 시각
   * @param endAt 방문 종료 시각
   * @param transport 이동수단
   */
  public CreateCourseCommand {
    themes = List.copyOf(themes);
  }
}
