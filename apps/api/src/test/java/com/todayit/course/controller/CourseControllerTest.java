package com.todayit.course.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.todayit.common.exception.GlobalExceptionHandler;
import com.todayit.course.entity.CourseStatus;
import com.todayit.course.entity.CourseTransport;
import com.todayit.course.exception.CourseErrorCode;
import com.todayit.course.exception.CourseException;
import com.todayit.course.exception.InvalidRegionPathException;
import com.todayit.course.service.CourseFacade;
import com.todayit.course.service.command.CreateCourseCommand;
import com.todayit.course.service.model.CreateCourseResult;
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
  @DisplayName("인증된 회원으로 코스를 생성하고 DRAFT와 빈 장소 목록을 반환한다")
  void createsDraftUsingAuthenticatedMember() throws Exception {
    // given
    LocalDateTime createdAt = LocalDateTime.of(2026, 11, 17, 12, 0);
    when(courseFacade.createCourse(eq("member-1"), any()))
        .thenReturn(new CreateCourseResult(12, "종로 데이트", CourseStatus.DRAFT, null, createdAt));
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
        .andExpect(jsonPath("$.data.title").value("종로 데이트"))
        .andExpect(jsonPath("$.data.status").value("DRAFT"))
        .andExpect(jsonPath("$.data.places").isEmpty())
        .andExpect(jsonPath("$.data.courseSummary").value(org.hamcrest.Matchers.nullValue()))
        .andExpect(jsonPath("$.data.createdAt").isNotEmpty());
    verify(courseFacade)
        .createCourse(
            "member-1",
            new CreateCourseCommand(
                "종로 데이트",
                "11110",
                List.of("감성적", "여유로움"),
                LocalDateTime.of(2026, 12, 1, 10, 0),
                null,
                240,
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
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
  @DisplayName("지역 코드 대신 지역명을 입력하면 400을 반환한다")
  void rejectsRegionName() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "region": "서울",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_REGION"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("지역 코드 길이가 올바르지 않으면 400을 반환한다")
  void rejectsInvalidRegionLength() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "region": "111",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_REGION"));
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
          "region": "11110",
          "themes": null,
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
          "region": "11110",
          "themes": [],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움",
            "아늑함"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
          "region": "11110",
          "themes": [
            "감성적",
            "감성적"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
          "region": "11110",
          "themes": [
            null
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": null,
          "durationMinutes": 240,
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
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "wrong",
          "durationMinutes": 240,
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
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
  @DisplayName("종료 시각과 가용시간이 모두 없으면 400을 반환한다")
  void rejectsMissingTimeCondition() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": null,
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
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_TIME_CONDITION"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("가용시간이 0분이면 400을 반환한다")
  void rejectsZeroDuration() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 0,
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
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_DURATION"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("가용시간이 음수이면 400을 반환한다")
  void rejectsNegativeDuration() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": -1,
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
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_DURATION"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("종료 시각과 가용시간을 함께 입력하면 400을 반환한다")
  void rejectsBothTimeConditions() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
          "transport": "WALKING",
          "endAt": "2026-12-01T14:00:00"
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
        .andExpect(jsonPath("$.code").value("INVALID_COURSE_TIME_CONDITION"));
    verifyNoInteractions(courseFacade);
  }

  @Test
  @DisplayName("종료 시각이 시작 시각과 같으면 400을 반환한다")
  void rejectsEqualEndTime() throws Exception {
    // given
    String request =
        """
        {
          "title": "  종로 데이트  ",
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": null,
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
          "region": "11110",
          "themes": [
            "감성적",
            "여유로움"
          ],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": null,
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
          "region": "11110",
          "themes": ["감성적"],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
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
  @DisplayName("존재하지 않는 지역이면 지역 전용 오류 코드를 반환한다")
  void returnsInvalidRegionPathCode() throws Exception {
    // given
    String request =
        """
        {
          "title": "종로 데이트",
          "region": "11110",
          "themes": ["감성적"],
          "startAt": "2026-12-01T10:00:00",
          "durationMinutes": 240,
          "transport": "WALKING"
        }
        """;
    when(courseFacade.createCourse(eq("member-1"), any()))
        .thenThrow(new InvalidRegionPathException());

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
        .andExpect(jsonPath("$.code").value("INVALID_REGION_PATH"))
        .andExpect(jsonPath("$.message").value("지역 필터 값이 올바르지 않습니다."));
  }
}
