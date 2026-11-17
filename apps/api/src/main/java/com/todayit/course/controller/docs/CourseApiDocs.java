package com.todayit.course.controller.docs;

import com.todayit.common.openapi.OpenApiConfig;
import com.todayit.common.response.CommonResponse;
import com.todayit.common.response.ErrorResponse;
import com.todayit.course.dto.request.CreateCourseRequest;
import com.todayit.course.dto.response.CreateCourseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

/** 코스 API 명세입니다. */
@Tag(name = "코스", description = "코스 API")
public interface CourseApiDocs {

  /**
   * 입력 조건으로 비공개 DRAFT를 생성합니다.
   *
   * @param request 코스 생성 조건
   * @param authentication 인증된 작성자 정보
   * @return 생성한 코스 정보
   */
  @Operation(
      summary = "코스 생성",
      description =
          "로그인한 회원이 입력한 정보로 작성 중인 코스를 만듭니다. 생성한 코스는 비공개이며 상태는 DRAFT입니다. "
              + "코스명, 지역, 콘셉트, 시작 시각, 이동수단은 필수이고, "
              + "방문을 마칠 시각(endAt) 또는 코스 전체에 사용할 시간(durationMinutes, 분) 중 하나만 입력합니다. "
              + "생성 직후에는 장소 목록이 비어 있고 요약 설명은 null입니다. "
              + "같은 요청을 다시 보내면 새로운 코스가 생성됩니다.",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH))
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "비공개 DRAFT 생성 성공",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":true,"data":{"courseId":1,"title":"종로 데이트","status":"DRAFT","places":[],"courseSummary":null,"createdAt":"2026-11-17T12:00:00"}}
                    """))),
    @ApiResponse(
        responseCode = "400",
        description = "입력 조건 오류 또는 지원하지 않는 지역·콘셉트",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                  @ExampleObject(
                      name = "코스명 누락·공백·길이 초과",
                      value =
                          """
                      {"success":false,"code":"INVALID_COURSE_TITLE","message":"코스명은 1자 이상 100자 이하이어야 합니다."}
                      """),
                  @ExampleObject(
                      name = "지역 코드 형식 오류",
                      value =
                          """
                      {"success":false,"code":"INVALID_COURSE_REGION","message":"지역은 시도 2자리 또는 시도·시군구 5자리 코드이어야 합니다."}
                      """),
                  @ExampleObject(
                      name = "존재하지 않는 지역 코드",
                      value =
                          """
                      {"success":false,"code":"INVALID_REGION_PATH","message":"지역 필터 값이 올바르지 않습니다."}
                      """),
                  @ExampleObject(
                      name = "콘셉트 누락·빈 값·중복·개수 초과",
                      value =
                          """
                      {"success":false,"code":"INVALID_COURSE_THEMES","message":"콘셉트는 중복 없이 1개 이상 2개 이하로 입력해야 합니다."}
                      """),
                  @ExampleObject(
                      name = "등록되지 않은 콘셉트",
                      value =
                          """
                      {"success":false,"code":"UNSUPPORTED_COURSE_THEME","message":"지원하지 않는 콘셉트가 포함되어 있습니다."}
                      """),
                  @ExampleObject(
                      name = "시작 시각 누락",
                      value =
                          """
                      {"success":false,"code":"COURSE_START_AT_REQUIRED","message":"시작 시각은 필수입니다."}
                      """),
                  @ExampleObject(
                      name = "이동수단 누락",
                      value =
                          """
                      {"success":false,"code":"COURSE_TRANSPORT_REQUIRED","message":"이동수단은 필수입니다."}
                      """),
                  @ExampleObject(
                      name = "종료 시각과 가용시간 동시 입력 또는 모두 누락",
                      value =
                          """
                      {"success":false,"code":"INVALID_COURSE_TIME_CONDITION","message":"종료 시각과 가용시간 중 하나만 입력해야 합니다."}
                      """),
                  @ExampleObject(
                      name = "종료 시각이 시작 시각 이전이거나 동일",
                      value =
                          """
                      {"success":false,"code":"INVALID_COURSE_END_AT","message":"종료 시각은 시작 시각 이후이어야 합니다."}
                      """),
                  @ExampleObject(
                      name = "가용시간이 0분 이하",
                      value =
                          """
                      {"success":false,"code":"INVALID_COURSE_DURATION","message":"가용시간은 1분 이상이어야 합니다."}
                      """),
                  @ExampleObject(
                      name = "JSON·날짜 형식 또는 이동수단 enum 오류",
                      value =
                          """
                      {"success":false,"code":"INVALID_REQUEST","message":"요청값이 올바르지 않습니다."}
                      """)
                })),
    @ApiResponse(
        responseCode = "401",
        description = "인증 정보 누락, 유효하지 않은 토큰 또는 만료된 로그인 세션",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        name = "인증 필요",
                        value =
                            """
                    {"success":false,"code":"AUTHENTICATION_REQUIRED","message":"인증이 필요합니다."}
                    """)))
  })
  ResponseEntity<CommonResponse<CreateCourseResponse>> createCourse(
      CreateCourseRequest request, Authentication authentication);
}
