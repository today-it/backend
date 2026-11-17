package com.todayit.course.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.todayit.common.exception.GlobalExceptionHandler;
import com.todayit.course.entity.CourseStatus;
import com.todayit.course.entity.CourseTransport;
import com.todayit.course.entity.CourseVisibility;
import com.todayit.course.exception.CourseErrorCode;
import com.todayit.course.exception.CourseException;
import com.todayit.course.exception.CourseNotFoundException;
import com.todayit.course.service.CourseFacade;
import com.todayit.course.service.command.CreateCourseCommand;
import com.todayit.course.service.model.CourseDetailResult;
import com.todayit.course.service.model.CoursePlaceDetailResult;
import com.todayit.course.service.model.CreateCourseResult;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@DisplayName("코스 생성 HTTP 계약")
class CourseControllerTest {
  @Mock private CourseFacade courseFacade;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    mvc =
        MockMvcBuilders.standaloneSetup(new CourseController(courseFacade))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  @DisplayName("인증된 회원으로 코스를 생성하고 DRAFT의 courseId를 반환한다")
  void createsDraftUsingAuthenticatedMember() throws Exception {
    // given
    LocalDateTime createdAt = LocalDateTime.of(2026, 11, 17, 12, 0);
    when(courseFacade.createCourse(eq("member-1"), any())).thenReturn(new CreateCourseResult(12));
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "endAt": "2026-12-01T14:00:00",
          "transport": "WALKING"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.courseId").value(12))
        .andExpect(jsonPath("$.data.title").doesNotExist())
        .andExpect(jsonPath("$.data.places").doesNotExist());
    verify(courseFacade)
        .createCourse(
            "member-1",
            new CreateCourseCommand(
                "종로 데이트",
                List.of("감성적", "여유로움"),
                LocalDateTime.of(2026, 12, 1, 10, 0),
                LocalDateTime.of(2026, 12, 1, 14, 0),
                CourseTransport.WALKING));
  }

  @Test
  @DisplayName("코스명이 없으면 400을 반환한다")
  void rejectsMissingTitle() throws Exception {
    // given
    String request =
        """
        {
          "title": null,
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "endAt": "2026-12-01T14:00:00",
          "transport": "WALKING"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_TITLE"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("코스명이 공백이면 400을 반환한다")
  void rejectsBlankTitle() throws Exception {
    // given
    String request =
        """
        {
          "title": " ",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "endAt": "2026-12-01T14:00:00",
          "transport": "WALKING"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_TITLE"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("콘셉트가 없으면 400을 반환한다")
  void rejectsMissingThemes() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": null,
          "startAt": "2026-12-01T10:00:00",
          "endAt": "2026-12-01T14:00:00",
          "transport": "WALKING"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_THEMES"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("콘셉트 목록이 비어 있으면 400을 반환한다")
  void rejectsEmptyThemes() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": [],
          "startAt": "2026-12-01T10:00:00",
          "endAt": "2026-12-01T14:00:00",
          "transport": "WALKING"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_THEMES"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("콘셉트가 2개를 초과하면 400을 반환한다")
  void rejectsTooManyThemes() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": [
            "감성적",
            "여유로움",
            "아늑함"
          ],
          "startAt": "2026-12-01T10:00:00",
          "endAt": "2026-12-01T14:00:00",
          "transport": "WALKING"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_THEMES"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("같은 콘셉트를 중복 입력하면 400을 반환한다")
  void rejectsDuplicateThemes() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": [
            "감성적",
            "감성적"
          ],
          "startAt": "2026-12-01T10:00:00",
          "endAt": "2026-12-01T14:00:00",
          "transport": "WALKING"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_THEMES"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("콘셉트 목록에 null이 있으면 400을 반환한다")
  void rejectsNullTheme() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": [
            null
          ],
          "startAt": "2026-12-01T10:00:00",
          "endAt": "2026-12-01T14:00:00",
          "transport": "WALKING"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_THEMES"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("시작 시각이 없으면 400을 반환한다")
  void rejectsMissingStartTime() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": null,
          "endAt": "2026-12-01T14:00:00",
          "transport": "WALKING"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COURSE_START_AT_REQUIRED"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("시작 시각 형식이 올바르지 않으면 400을 반환한다")
  void rejectsMalformedStartTime() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "wrong",
          "endAt": "2026-12-01T14:00:00",
          "transport": "WALKING"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("이동수단이 없으면 400을 반환한다")
  void rejectsMissingTransport() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "endAt": "2026-12-01T14:00:00",
          "transport": null
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COURSE_TRANSPORT_REQUIRED"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("지원하지 않는 이동수단이면 400을 반환한다")
  void rejectsUnknownTransport() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "endAt": "2026-12-01T14:00:00",
          "transport": "PLANE"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("종료 시각이 없어도 DRAFT를 정상 생성한다")
  void createsDraftWithoutEndTime() throws Exception {
    // given
    LocalDateTime createdAt = LocalDateTime.of(2026, 11, 17, 12, 0);
    when(courseFacade.createCourse(eq("member-1"), any())).thenReturn(new CreateCourseResult(12));
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "endAt": null,
          "transport": "WALKING"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.courseId").value(12));
    verify(courseFacade)
        .createCourse(
            "member-1",
            new CreateCourseCommand(
                "종로 데이트",
                List.of("감성적", "여유로움"),
                LocalDateTime.of(2026, 12, 1, 10, 0),
                null,
                CourseTransport.WALKING));
  }

  @Test
  @DisplayName("종료 시각이 시작 시각과 같으면 400을 반환한다")
  void rejectsEqualEndTime() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "transport": "WALKING",
          "endAt": "2026-12-01T10:00:00"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_END_AT"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("종료 시각이 시작 시각보다 이르면 400을 반환한다")
  void rejectsEarlierEndTime() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "transport": "WALKING",
          "endAt": "2026-11-30T10:00:00"
        }
        """;

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_END_AT"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("등록되지 않은 콘셉트이면 코스 전용 오류 코드를 반환한다")
  void returnsUnsupportedThemeCode() throws Exception {
    // given
    String request =
        """
        {
          "title": "종로 데이트",
          "themes": ["감성적"],
          "startAt": "2026-12-01T10:00:00",
          "endAt": "2026-12-01T14:00:00",
          "transport": "WALKING"
        }
        """;
    when(courseFacade.createCourse(eq("member-1"), any()))
        .thenThrow(new CourseException(CourseErrorCode.UNSUPPORTED_THEME));

    // when
    ResultActions result =
        mvc.perform(
            post("/api/v1/courses")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

    // then
    result
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("UNSUPPORTED_COURSE_THEME"))
        .andExpect(jsonPath("$.message").value("지원하지 않는 콘셉트가 포함되어 있습니다."));
  }

  @Test
  @DisplayName("회원 인증 상태로 코스 상세 정보를 정상 조회한다")
  void getsCourseDetailSuccessfully() throws Exception {
    // given
    LocalDateTime startAt = LocalDateTime.of(2026, 12, 1, 10, 0);
    LocalDateTime endAt = LocalDateTime.of(2026, 12, 1, 14, 0);
    CoursePlaceDetailResult place =
        new CoursePlaceDetailResult(
            1,
            10,
            "블루보틀 삼청",
            "CAFE_DESSERT",
            "서울특별시 종로구 북촌로 5길",
            new BigDecimal("37.58123400"),
            new BigDecimal("126.98123400"),
            "https://example.com/place.jpg",
            1,
            60);
    CourseDetailResult detail =
        new CourseDetailResult(
            1,
            "종로 데이트",
            "삼청동 카페 투어",
            CourseTransport.WALKING,
            startAt,
            endAt,
            CourseStatus.DRAFT,
            CourseVisibility.PRIVATE,
            5,
            "11",
            "서울특별시",
            "110",
            "종로구",
            List.of("감성적"),
            true,
            List.of(place),
            startAt.minusDays(1),
            null);
    when(courseFacade.getCourseDetail(1, "member-1")).thenReturn(detail);

    // when
    ResultActions result =
        mvc.perform(
            get("/api/v1/courses/1")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of())));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.courseId").value(1))
        .andExpect(jsonPath("$.data.title").value("종로 데이트"))
        .andExpect(jsonPath("$.data.courseSummary").value("삼청동 카페 투어"))
        .andExpect(jsonPath("$.data.transport").value("WALKING"))
        .andExpect(jsonPath("$.data.status").value("DRAFT"))
        .andExpect(jsonPath("$.data.visibility").value("PRIVATE"))
        .andExpect(jsonPath("$.data.sidoCode").value("11"))
        .andExpect(jsonPath("$.data.sidoName").value("서울특별시"))
        .andExpect(jsonPath("$.data.sigunguCode").value("110"))
        .andExpect(jsonPath("$.data.sigunguName").value("종로구"))
        .andExpect(jsonPath("$.data.themes[0]").value("감성적"))
        .andExpect(jsonPath("$.data.isWriter").value(true))
        .andExpect(jsonPath("$.data.places[0].coursePlaceId").value(1))
        .andExpect(jsonPath("$.data.places[0].name").value("블루보틀 삼청"))
        .andExpect(jsonPath("$.data.places[0].stayMinute").value(60));
  }

  @Test
  @DisplayName("비회원으로 공개 코스 상세 정보를 정상 조회하면 isWriter는 false이다")
  void getsPublicCourseDetailAsAnonymous() throws Exception {
    // given
    LocalDateTime startAt = LocalDateTime.of(2026, 12, 1, 10, 0);
    CourseDetailResult detail =
        new CourseDetailResult(
            1,
            "종로 데이트",
            null,
            CourseTransport.WALKING,
            startAt,
            null,
            CourseStatus.ACTIVE,
            CourseVisibility.PUBLIC,
            12,
            "11",
            "서울특별시",
            null,
            null,
            List.of("감성적"),
            false,
            List.of(),
            startAt.minusDays(1),
            null);
    when(courseFacade.getCourseDetail(1, null)).thenReturn(detail);

    // when
    ResultActions result = mvc.perform(get("/api/v1/courses/1"));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.courseId").value(1))
        .andExpect(jsonPath("$.data.isWriter").value(false))
        .andExpect(jsonPath("$.data.places").isEmpty());
  }

  @Test
  @DisplayName("존재하지 않거나 접근 권한이 없는 코스는 404와 COURSE_NOT_FOUND를 반환한다")
  void returnsNotFoundWhenCourseCannotBeAccessed() throws Exception {
    // given
    when(courseFacade.getCourseDetail(99, "member-1")).thenThrow(new CourseNotFoundException());

    // when
    ResultActions result =
        mvc.perform(
            get("/api/v1/courses/99")
                .principal(new UsernamePasswordAuthenticationToken("member-1", null, List.of())));

    // then
    result
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value("COURSE_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("코스를 찾을 수 없습니다."));
  }
}
