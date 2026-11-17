package com.todayit.course.dto.response;

import com.todayit.course.entity.CourseStatus;
import com.todayit.course.service.model.CreateCourseResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 코스 생성 응답입니다.
 *
 * @param courseId 코스 식별자
 * @param title 코스명
 * @param status DRAFT 상태
 * @param places 생성 시 비어 있는 장소 목록
 * @param courseSummary 코스 설명, AI 생성 전 null
 * @param createdAt 서버 생성 시각
 */
@Schema(description = "새로 생성한 임시 코스 정보입니다.")
public record CreateCourseResponse(
    @Schema(
            description = "생성된 코스의 식별자입니다.",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
        int courseId,
    @Schema(
            description = "앞뒤 공백을 제거해 저장한 코스 이름입니다.",
            example = "종로 데이트",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
    @Schema(
            description = "코스의 상태입니다. 생성 직후에는 작성 중인 상태인 DRAFT를 반환합니다.",
            example = "DRAFT",
            requiredMode = Schema.RequiredMode.REQUIRED)
        CourseStatus status,
    @Schema(
            description = "코스에 추가한 장소 목록입니다. 생성 직후에는 빈 배열을 반환합니다.",
            example = "[]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<Object> places,
    @Schema(
            description = "코스 요약 설명입니다. 생성 직후에는 null을 반환합니다.",
            nullable = true,
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String courseSummary,
    @Schema(
            description = "서버에서 코스를 생성한 날짜와 시각입니다.",
            example = "2026-11-17T12:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime createdAt) {

  /**
   * 응답의 장소 목록을 방어적으로 복사합니다.
   *
   * @param courseId 코스 식별자
   * @param title 코스명
   * @param status DRAFT 상태
   * @param places 생성 시 비어 있는 장소 목록
   * @param courseSummary 코스 설명, AI 생성 전 null
   * @param createdAt 서버 생성 시각
   */
  public CreateCourseResponse {
    places = List.copyOf(places);
  }

  /**
   * 생성 결과를 HTTP 응답으로 변환합니다.
   *
   * @param result 생성 결과
   * @return 코스 생성 응답
   */
  public static CreateCourseResponse from(CreateCourseResult result) {
    return new CreateCourseResponse(
        result.courseId(),
        result.title(),
        result.status(),
        List.of(),
        result.courseSummary(),
        result.createdAt());
  }
}
