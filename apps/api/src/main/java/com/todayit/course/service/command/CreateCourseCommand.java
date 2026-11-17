package com.todayit.course.service.command;

import com.todayit.course.entity.CourseTransport;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 코스 생성 조건입니다.
 *
 * @param title 코스명
 * @param region 시도 2자리 또는 시도·시군구 5자리 코드
 * @param themes 콘셉트 이름 목록
 * @param startAt 방문 시작 시각
 * @param endAt 방문 종료 시각
 * @param durationMinutes 가용시간(분)
 * @param transport 이동수단
 */
public record CreateCourseCommand(
    String title,
    String region,
    List<String> themes,
    LocalDateTime startAt,
    LocalDateTime endAt,
    Integer durationMinutes,
    CourseTransport transport) {

  /**
   * 콘셉트 목록을 방어적으로 복사합니다.
   *
   * @param title 코스명
   * @param region 시도 2자리 또는 시도·시군구 5자리 코드
   * @param themes 콘셉트 이름 목록
   * @param startAt 방문 시작 시각
   * @param endAt 방문 종료 시각
   * @param durationMinutes 가용시간(분)
   * @param transport 이동수단
   */
  public CreateCourseCommand {
    themes = List.copyOf(themes);
  }
}
