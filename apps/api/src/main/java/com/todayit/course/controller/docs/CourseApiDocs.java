package com.todayit.course.controller.docs;

import com.todayit.common.openapi.OpenApiConfig;
import com.todayit.common.response.CommonResponse;
import com.todayit.common.response.ErrorResponse;
import com.todayit.course.dto.request.AddCoursePlaceRequest;
import com.todayit.course.dto.request.CreateCourseRequest;
import com.todayit.course.dto.request.UpdateCoursePlaceOrdersRequest;
import com.todayit.course.dto.response.AddCoursePlaceResponse;
import com.todayit.course.dto.response.CopyCourseResponse;
import com.todayit.course.dto.response.CourseDetailResponse;
import com.todayit.course.dto.response.CourseLikeResponse;
import com.todayit.course.dto.response.CreateCourseResponse;
import com.todayit.course.dto.response.RemoveCoursePlaceResponse;
import com.todayit.course.dto.response.UpdateCoursePlaceOrdersResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

/** 코스 생성, 조회 및 관리 API 문서입니다. */
@Tag(name = "코스", description = "코스 생성, 조회 및 관리 API")
public interface CourseApiDocs {

  /**
   * 새로운 코스를 DRAFT 상태로 생성합니다.
   *
   * @param request 코스 생성 요청 본문
   * @param authentication 인증된 회원 정보
   * @return 생성된 코스 식별자
   */
  @Operation(
      summary = "새 코스 생성",
      description = "주어진 조건(제목, 이동수단, 일정, 콘셉트 등)으로 새로운 DRAFT 코스를 생성합니다.",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH))
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "코스 생성 성공",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples =
                    @ExampleObject(
                        value =
                            """
                    {
                      "success": true,
                      "data": {
                        "courseId": 1
                      }
                    }
                    """))),
    @ApiResponse(
        responseCode = "400",
        description = "유효하지 않은 입력값",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                  @ExampleObject(
                      name = "유효하지 않은 코스명",
                      value =
                          """
                      {"success":false,"code":"INVALID_COURSE_TITLE","message":"코스명은 1자 이상 100자 이하이어야 합니다."}
                      """),
                  @ExampleObject(
                      name = "유효하지 않은 콘셉트",
                      value =
                          """
                      {"success":false,"code":"INVALID_COURSE_THEMES","message":"콘셉트는 중복 없이 1개 이상 2개 이하로 입력해야 합니다."}
                      """),
                  @ExampleObject(
                      name = "지원하지 않는 콘셉트",
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
                      name = "유효하지 않은 종료 시각",
                      value =
                          """
                      {"success":false,"code":"INVALID_COURSE_END_AT","message":"종료 시각은 시작 시각 이후이어야 합니다."}
                      """),
                  @ExampleObject(
                      name = "이동수단 누락",
                      value =
                          """
                      {"success":false,"code":"COURSE_TRANSPORT_REQUIRED","message":"이동수단은 필수입니다."}
                      """)
                })),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"UNAUTHORIZED","message":"인증이 필요합니다."}
                    """))),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"INTERNAL_SERVER_ERROR","message":"서버 내부 오류가 발생했습니다."}
                    """)))
  })
  ResponseEntity<CommonResponse<CreateCourseResponse>> createCourse(
      CreateCourseRequest request, @Parameter(hidden = true) Authentication authentication);

  /**
   * 코스 식별자로 코스 상세 정보를 조회합니다.
   *
   * @param courseId 코스 식별자
   * @param authentication 선택 인증 정보
   * @return 코스 상세 정보
   */
  @Operation(
      summary = "코스 상세 조회",
      description = "코스 식별자로 코스의 상세 정보, 연관된 장소 목록 및 방문 순서를 조회합니다.",
      security = {
        @SecurityRequirement(name = ""),
        @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
      })
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "코스 상세 조회 성공",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples =
                    @ExampleObject(
                        value =
                            """
                    {
                      "success": true,
                      "data": {
                        "courseId": 1,
                        "title": "데이트 코스",
                        "courseSummary": "성수동 감성 데이트 코스입니다.",
                        "transport": "CAR",
                        "startAt": "2026-11-06T10:00:00",
                        "endAt": "2026-11-06T18:00:00",
                        "status": "DRAFT",
                        "visibility": "PUBLIC",
                        "viewCount": 0,
                        "sidoCode": "11",
                        "sidoName": "서울특별시",
                        "sigunguCode": "110",
                        "sigunguName": "종로구",
                        "themes": ["EMOTIONAL"],
                        "canEdit": true,
                        "places": [
                          {
                            "coursePlaceId": 1,
                            "placeId": 10,
                            "name": "카페 A",
                            "category": "CAFE",
                            "address": "서울 성동구 1길 1",
                            "latitude": 37.5445,
                            "longitude": 127.0557,
                            "imageUrl": "https://example.com/a.jpg",
                            "order": 1,
                            "stayMinute": 60
                          }
                        ],
                        "liked": false,
                        "scrapped": false,
                        "beforeVisit": ["방문 전 예약 여부를 확인하세요."]
                      }
                    }
                    """))),
    @ApiResponse(
        responseCode = "404",
        description = "코스를 찾을 수 없거나 접근 권한 없음",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"COURSE_NOT_FOUND","message":"코스를 찾을 수 없습니다."}
                    """))),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"INTERNAL_SERVER_ERROR","message":"서버 내부 오류가 발생했습니다."}
                    """)))
  })
  ResponseEntity<CommonResponse<CourseDetailResponse>> getCourseDetail(
      @Parameter(description = "코스 식별자입니다.", example = "1", required = true) int courseId,
      @Parameter(hidden = true) Authentication authentication);

  /**
   * 코스에 새 장소를 추가합니다.
   *
   * @param courseId 코스 식별자
   * @param request 장소 추가 요청 본문
   * @param authentication 인증된 회원 정보
   * @return 추가된 장소 상세 및 최신 전체 장소 목록
   */
  @Operation(
      summary = "코스에 장소 추가",
      description =
          "작성자가 소유한 코스에 새 장소를 추가합니다. 방문 순서를 지정하지 않으면 맨 뒤에 추가되며, 중간에 삽입하면 이후 장소들의 순서가 자동으로 조정됩니다.",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH))
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "장소 추가 성공",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples =
                    @ExampleObject(
                        value =
                            """
                    {
                      "success": true,
                      "data": {
                        "addedPlace": {
                          "coursePlaceId": 5,
                          "placeId": 2,
                          "name": "성수 카페",
                          "category": "CAFE",
                          "address": "서울특별시 성동구 성수이로 10",
                          "latitude": 37.5445,
                          "longitude": 127.0557,
                          "imageUrl": "https://placehold.co/1200x800?text=Cafe",
                          "order": 1,
                          "stayMinute": 30
                        },
                        "places": [
                          {
                            "coursePlaceId": 5,
                            "placeId": 2,
                            "name": "성수 카페",
                            "category": "CAFE",
                            "address": "서울특별시 성동구 성수이로 10",
                            "latitude": 37.5445,
                            "longitude": 127.0557,
                            "imageUrl": "https://placehold.co/1200x800?text=Cafe",
                            "order": 1,
                            "stayMinute": 30
                          }
                        ]
                      }
                    }
                    """))),
    @ApiResponse(
        responseCode = "400",
        description = "유효하지 않은 장소 ID, 순서, 체류 시간 또는 장소 중복 횟수 초과",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                  @ExampleObject(
                      name = "유효하지 않은 장소 식별자",
                      value =
                          """
                      {"success":false,"code":"INVALID_PLACE_ID","message":"장소 식별자는 1 이상이어야 합니다."}
                      """),
                  @ExampleObject(
                      name = "유효하지 않은 방문 순서",
                      value =
                          """
                      {"success":false,"code":"INVALID_COURSE_PLACE_ORDER","message":"방문 순서는 1 이상이어야 합니다."}
                      """),
                  @ExampleObject(
                      name = "유효하지 않은 체류 시간",
                      value =
                          """
                      {"success":false,"code":"INVALID_STAY_MINUTE","message":"체류 시간은 0분 이상 1440분 이하이어야 합니다."}
                      """),
                  @ExampleObject(
                      name = "동일 장소 중복 횟수 초과",
                      value =
                          """
                      {"success":false,"code":"MAX_PLACE_DUPLICATION_EXCEEDED","message":"동일한 장소는 코스에 최대 2회까지만 추가할 수 있습니다."}
                      """)
                })),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"UNAUTHORIZED","message":"인증이 필요합니다."}
                    """))),
    @ApiResponse(
        responseCode = "404",
        description = "코스를 찾을 수 없거나 접근 권한 없음, 또는 장소를 찾을 수 없음",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                  @ExampleObject(
                      name = "코스를 찾을 수 없음",
                      value =
                          """
                      {"success":false,"code":"COURSE_NOT_FOUND","message":"코스를 찾을 수 없습니다."}
                      """),
                  @ExampleObject(
                      name = "장소를 찾을 수 없음",
                      value =
                          """
                      {"success":false,"code":"PLACE_NOT_FOUND","message":"장소를 찾을 수 없습니다."}
                      """)
                })),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"INTERNAL_SERVER_ERROR","message":"서버 내부 오류가 발생했습니다."}
                    """)))
  })
  ResponseEntity<CommonResponse<AddCoursePlaceResponse>> addPlaceToCourse(
      @Parameter(description = "코스 식별자입니다.", example = "1", required = true) int courseId,
      AddCoursePlaceRequest request,
      @Parameter(hidden = true) Authentication authentication);

  /**
   * 코스 장소들의 방문 순서를 일괄 변경합니다.
   *
   * @param courseId 코스 식별자
   * @param request 새로운 코스 장소 식별자 순서 목록
   * @param authentication 인증된 작성자 정보
   * @return 순서 변경 후 정렬된 전체 장소 목록
   */
  @Operation(
      summary = "코스 장소 순서 일괄 변경",
      description =
          "코스 내 모든 장소의 방문 순서를 전달받은 ID 순서대로 일괄 변경합니다. 전달된 목록은 현재 코스에 등록된 전체 장소 식별자를 중복 없이 모두 포함해야 합니다.",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "장소 순서 일괄 변경 성공",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples =
                    @ExampleObject(
                        value =
                            """
                    {
                      "success": true,
                      "data": {
                        "places": [
                          {
                            "coursePlaceId": 3,
                            "placeId": 2,
                            "name": "성수 카페",
                            "category": "CAFE",
                            "address": "서울특별시 성동구 성수이로 10",
                            "latitude": 37.5445,
                            "longitude": 127.0557,
                            "imageUrl": "https://placehold.co/1200x800?text=Cafe",
                            "order": 1,
                            "stayMinute": 60
                          },
                          {
                            "coursePlaceId": 1,
                            "placeId": 1,
                            "name": "서울 숲",
                            "category": "NATURE_WALK",
                            "address": "서울특별시 성동구 뚝섬로 273",
                            "latitude": 37.5443,
                            "longitude": 127.0374,
                            "imageUrl": "https://placehold.co/1200x800?text=Forest",
                            "order": 2,
                            "stayMinute": 120
                          }
                        ]
                      }
                    }
                    """))),
    @ApiResponse(
        responseCode = "400",
        description = "유효하지 않은 장소 ID 목록 형식, 개수 불일치 또는 중복 ID 포함",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        name = "순서 목록 형식 오류 또는 개수 불일치",
                        value =
                            """
                    {"success":false,"code":"INVALID_COURSE_PLACE_ORDER","message":"방문 순서는 1 이상이어야 합니다."}
                    """))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"UNAUTHORIZED","message":"인증이 필요합니다."}
                    """))),
    @ApiResponse(
        responseCode = "404",
        description = "코스를 찾을 수 없거나 접근 권한 없음, 또는 코스 내 장소를 찾을 수 없음",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                  @ExampleObject(
                      name = "코스를 찾을 수 없음",
                      value =
                          """
                      {"success":false,"code":"COURSE_NOT_FOUND","message":"코스를 찾을 수 없습니다."}
                      """),
                  @ExampleObject(
                      name = "코스 내 장소를 찾을 수 없음",
                      value =
                          """
                      {"success":false,"code":"COURSE_PLACE_NOT_FOUND","message":"코스 내 장소를 찾을 수 없습니다."}
                      """)
                })),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"INTERNAL_SERVER_ERROR","message":"서버 내부 오류가 발생했습니다."}
                    """)))
  })
  ResponseEntity<CommonResponse<UpdateCoursePlaceOrdersResponse>> updateCoursePlaceOrders(
      @Parameter(description = "코스 식별자입니다.", example = "1", required = true) int courseId,
      UpdateCoursePlaceOrdersRequest request,
      @Parameter(hidden = true) Authentication authentication);

  /**
   * 코스에서 특정 장소를 삭제합니다.
   *
   * @param courseId 코스 식별자
   * @param coursePlaceId 삭제할 코스 장소 식별자
   * @param authentication 인증된 작성자 정보
   * @return 삭제된 장소 식별자 및 재조정된 남은 장소 목록
   */
  @Operation(
      summary = "코스 장소 삭제",
      description = "코스에 등록된 특정 장소를 삭제하고 이후 장소들의 방문 순서를 1씩 앞당겨 재조정합니다.",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "장소 삭제 성공",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples =
                    @ExampleObject(
                        value =
                            """
                    {
                      "success": true,
                      "data": {
                        "deletedCoursePlaceId": 2,
                        "places": [
                          {
                            "coursePlaceId": 1,
                            "placeId": 1,
                            "name": "서울 숲",
                            "category": "NATURE_WALK",
                            "address": "서울특별시 성동구 뚝섬로 273",
                            "latitude": 37.5443,
                            "longitude": 127.0374,
                            "imageUrl": "https://placehold.co/1200x800?text=Forest",
                            "order": 1,
                            "stayMinute": 120
                          }
                        ]
                      }
                    }
                    """))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"UNAUTHORIZED","message":"인증이 필요합니다."}
                    """))),
    @ApiResponse(
        responseCode = "404",
        description = "코스를 찾을 수 없거나 접근 권한 없음, 또는 삭제할 코스 장소를 찾을 수 없음",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                  @ExampleObject(
                      name = "코스를 찾을 수 없음",
                      value =
                          """
                      {"success":false,"code":"COURSE_NOT_FOUND","message":"코스를 찾을 수 없습니다."}
                      """),
                  @ExampleObject(
                      name = "코스 내 장소를 찾을 수 없음",
                      value =
                          """
                      {"success":false,"code":"COURSE_PLACE_NOT_FOUND","message":"코스 내 장소를 찾을 수 없습니다."}
                      """)
                })),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"INTERNAL_SERVER_ERROR","message":"서버 내부 오류가 발생했습니다."}
                    """)))
  })
  ResponseEntity<CommonResponse<RemoveCoursePlaceResponse>> deleteCoursePlace(
      @Parameter(description = "코스 식별자입니다.", example = "1", required = true) int courseId,
      @Parameter(description = "삭제할 코스 장소 식별자입니다.", example = "1", required = true)
          int coursePlaceId,
      @Parameter(hidden = true) Authentication authentication);

  /**
   * 코스를 영구 삭제합니다.
   *
   * @param courseId 삭제할 코스 식별자
   * @param authentication 인증된 작성자 정보
   * @return 204 No Content
   */
  @Operation(
      summary = "코스 삭제",
      description = "코스를 영구 삭제(Hard Delete)합니다. 관련된 코스 장소, 코스 테마, 지역 연결, 좋아요, 스크랩 데이터도 함께 삭제됩니다.",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH))
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "코스 삭제 성공"),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"UNAUTHORIZED","message":"인증이 필요합니다."}
                    """))),
    @ApiResponse(
        responseCode = "404",
        description = "코스를 찾을 수 없거나 접근 권한 없음",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"COURSE_NOT_FOUND","message":"코스를 찾을 수 없습니다."}
                    """))),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"INTERNAL_SERVER_ERROR","message":"서버 내부 오류가 발생했습니다."}
                    """)))
  })
  ResponseEntity<Void> deleteCourse(
      @Parameter(description = "삭제할 코스 식별자입니다.", example = "1", required = true) int courseId,
      @Parameter(hidden = true) Authentication authentication);

  /**
   * 확정된 코스를 새로운 DRAFT 코스로 복사합니다.
   *
   * @param courseId 복사할 원본 코스 식별자
   * @param authentication 인증된 회원 정보
   * @return 복사되어 새로 생성된 코스 상세 정보
   */
  @Operation(
      summary = "코스 불러오기(복사)",
      description =
          "공개 확정 코스 또는 본인의 비공개 확정 코스를 바탕으로 새로운 DRAFT 코스를 생성합니다. 장소 목록과 방문 순서는 유지되며, 제목, 이동수단, 일정, 체류시간은 초기화됩니다.",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH))
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "코스 복사 성공",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples =
                    @ExampleObject(
                        value =
                            """
                    {
                      "success": true,
                      "data": {
                        "courseId": 4,
                        "originalCourseId": 1,
                        "title": null,
                        "status": "DRAFT",
                        "places": [
                          {
                            "coursePlaceId": 11,
                            "placeId": 6,
                            "name": "서울 전시 공간",
                            "category": "EXHIBITION_MUSEUM",
                            "address": "서울특별시 종로구 율곡로 30",
                            "latitude": 37.577,
                            "longitude": 126.981,
                            "imageUrl": "https://placehold.co/1200x800?text=Exhibition",
                            "order": 1,
                            "stayMinute": null
                          }
                        ],
                        "createdAt": "2026-11-19T17:32:45"
                      }
                    }
                    """))),
    @ApiResponse(
        responseCode = "400",
        description = "DRAFT 상태의 코스를 복사하려고 시도한 경우",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        name = "임시저장 코스 복사 불가",
                        value =
                            """
                    {"success":false,"code":"CANNOT_COPY_DRAFT","message":"임시저장 코스는 복사할 수 없습니다."}
                    """))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 필요",
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
                    """))),
    @ApiResponse(
        responseCode = "404",
        description = "코스를 찾을 수 없거나 타인의 비공개 코스에 접근한 경우",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        name = "코스를 찾을 수 없음",
                        value =
                            """
                    {"success":false,"code":"COURSE_NOT_FOUND","message":"코스를 찾을 수 없습니다."}
                    """))),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            "{\"success\":false,\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
  })
  ResponseEntity<CommonResponse<CopyCourseResponse>> copyCourse(
      @Parameter(description = "복사할 코스 식별자입니다.", example = "1", required = true) int courseId,
      @Parameter(hidden = true) Authentication authentication);

  /**
   * 인증된 회원이 코스에 좋아요를 등록합니다.
   *
   * @param courseId 코스 식별자
   * @param authentication 인증된 회원 정보
   * @return 코스 좋아요 결과
   */
  @Operation(
      summary = "코스 좋아요 등록",
      description = "인증된 회원이 코스에 좋아요를 등록합니다. 이미 좋아요를 누른 코스이면 409를 반환합니다.",
      security = @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH))
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "좋아요 생성 성공",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples =
                    @ExampleObject(
                        value =
                            """
                    {
                      "success": true,
                      "data": {
                        "courseId": 1,
                        "liked": true,
                        "likeCount": 1
                      }
                    }
                    """))),
    @ApiResponse(
        responseCode = "401",
        description = "인증 실패",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"UNAUTHORIZED","message":"인증이 필요합니다."}
                    """))),
    @ApiResponse(
        responseCode = "404",
        description = "코스가 없거나 비활성·삭제 상태이거나 접근 권한이 없음",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"COURSE_NOT_FOUND","message":"코스를 찾을 수 없습니다."}
                    """))),
    @ApiResponse(
        responseCode = "409",
        description = "이미 좋아요를 누른 코스",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            """
                    {"success":false,"code":"COURSE_ALREADY_LIKED","message":"이미 좋아요를 눌렀습니다."}
                    """))),
    @ApiResponse(
        responseCode = "500",
        description = "서버 내부 오류",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples =
                    @ExampleObject(
                        value =
                            "{\"success\":false,\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"서버 내부 오류가 발생했습니다.\"}")))
  })
  ResponseEntity<CommonResponse<CourseLikeResponse>> likeCourse(
      @Parameter(description = "코스 식별자입니다.", example = "1", required = true) int courseId,
      @Parameter(hidden = true) Authentication authentication);
}
