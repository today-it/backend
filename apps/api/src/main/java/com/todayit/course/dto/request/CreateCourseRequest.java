package com.todayit.course.dto.request;

import com.todayit.course.entity.CourseTransport;
import com.todayit.course.exception.CourseErrorCode;
import com.todayit.course.exception.CourseException;
import com.todayit.course.service.command.CreateCourseCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

/**
 * 코스 생성 요청입니다.
 *
 * @param title 코스명, 앞뒤 공백 제거 후 1~100자
 * @param region 시도 2자리 또는 시도·시군구 5자리 코드
 * @param themes 중복 없는 콘셉트 1~2개
 * @param startAt 방문 시작 시각
 * @param endAt 방문 종료 시각, 가용시간과 택일
 * @param durationMinutes 양의 정수인 가용시간(분), 종료 시각과 택일
 * @param transport 이동수단
 */
@Schema(
    description =
        "새 코스를 만들 때 입력하는 정보입니다. 코스명, 지역, 콘셉트, 시작 시각, 이동수단은 필수입니다. "
            + "종료 시각 또는 코스 전체에 사용할 수 있는 시간 중 하나만 입력합니다.")
public record CreateCourseRequest(
    @Schema(
            description = "사용자가 정한 코스 이름입니다. 앞뒤 공백을 제거한 뒤 1~100자로 입력합니다.",
            example = "종로 데이트",
            minLength = 1,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED)
        String title,
    @Schema(
            description =
                "코스를 계획한 지역의 행정 코드입니다. 시도 전체는 2자리(예: 서울 11), "
                    + "특정 시군구는 시도 2자리와 시군구 3자리를 합친 5자리(예: 종로구 11110)로 입력합니다.",
            example = "11110",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String region,
    @Schema(
            description =
                "코스에서 느끼고 싶은 분위기의 이름을 중복 없이 1~2개 입력합니다. "
                    + "theme 테이블에 등록된 이름을 사용합니다. 초기 등록값은 조용함, 로맨틱, 아늑함, 감성적, 활기참, "
                    + "이색적, 여유로움, 고급스러움, 레트로, 자연친화적입니다.",
            example = "[\"감성적\", \"여유로움\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> themes,
    @Schema(
            description = "코스 방문을 시작할 날짜와 시각입니다. 2026-12-01T10:00:00처럼 날짜와 시각 사이에 T를 넣어 입력합니다.",
            example = "2026-12-01T10:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime startAt,
    @Schema(
            description =
                "코스 방문을 마칠 날짜와 시각입니다. 시작 시각보다 이후여야 하며, "
                    + "2026-12-01T14:00:00 형식으로 입력합니다. durationMinutes를 입력한 경우에는 생략합니다.",
            example = "2026-12-01T14:00:00",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true)
        LocalDateTime endAt,
    @Schema(
            description =
                "코스 전체에 사용할 수 있는 시간을 분 단위로 입력합니다. 예를 들어 240은 4시간입니다. "
                    + "1 이상의 정수만 허용하며, endAt을 입력한 경우에는 생략합니다.",
            minimum = "1",
            example = "240",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true)
        Integer durationMinutes,
    @Schema(
            description = "코스에서 사용할 이동수단입니다. 자동차는 CAR, 도보는 WALKING, 대중교통은 PUBLIC_TRANSPORT로 입력합니다.",
            example = "WALKING",
            requiredMode = Schema.RequiredMode.REQUIRED)
        CourseTransport transport) {

  private static final int MAX_TITLE_LENGTH = 100;
  private static final int MAX_THEME_COUNT = 2;

  /**
   * 필수 입력과 범위, 조건 간 관계를 검증합니다.
   *
   * @throws CourseException 입력 조건이 올바르지 않을 때
   */
  public void validate() {
    if (title == null || title.isBlank() || title.strip().length() > MAX_TITLE_LENGTH) {
      throw new CourseException(CourseErrorCode.INVALID_TITLE);
    }
    if (region == null || !region.matches("[0-9]{2}([0-9]{3})?")) {
      throw new CourseException(CourseErrorCode.INVALID_REGION);
    }
    if (themes == null
        || themes.isEmpty()
        || themes.size() > MAX_THEME_COUNT
        || themes.stream().anyMatch(theme -> theme == null || theme.isBlank())
        || new HashSet<>(themes).size() != themes.size()) {
      throw new CourseException(CourseErrorCode.INVALID_THEMES);
    }
    if (startAt == null) {
      throw new CourseException(CourseErrorCode.START_AT_REQUIRED);
    }
    if (transport == null) {
      throw new CourseException(CourseErrorCode.TRANSPORT_REQUIRED);
    }
    if ((endAt == null) == (durationMinutes == null)) {
      throw new CourseException(CourseErrorCode.INVALID_TIME_CONDITION);
    }
    if (endAt != null && !endAt.isAfter(startAt)) {
      throw new CourseException(CourseErrorCode.INVALID_END_AT);
    }
    if (durationMinutes != null && durationMinutes <= 0) {
      throw new CourseException(CourseErrorCode.INVALID_DURATION);
    }
  }

  /**
   * 검증된 요청을 서비스 입력으로 변환합니다.
   *
   * @return 코스 생성 조건
   */
  public CreateCourseCommand toCommand() {
    return new CreateCourseCommand(
        title.strip(), region, themes, startAt, endAt, durationMinutes, transport);
  }
}
