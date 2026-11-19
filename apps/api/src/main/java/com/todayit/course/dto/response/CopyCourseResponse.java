package com.todayit.course.dto.response;

import com.todayit.course.service.model.CopyCourseResult;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 코스 복사 응답입니다.
 *
 * @param courseId 복사되어 새로 생성된 코스 식별자
 */
@Schema(description = "새로 생성된 임시 코스 식별자 정보입니다.")
public record CopyCourseResponse(
    @Schema(
            description = "복사되어 생성된 코스의 식별자입니다.",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED)
        int courseId) {

  /**
   * 복사 결과를 HTTP 응답으로 변환합니다.
   *
   * @param result 복사 결과
   * @return 코스 복사 응답
   */
  public static CopyCourseResponse from(CopyCourseResult result) {
    return new CopyCourseResponse(result.courseId());
  }
}
