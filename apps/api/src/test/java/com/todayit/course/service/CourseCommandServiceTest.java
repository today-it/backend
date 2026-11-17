package com.todayit.course.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.todayit.course.entity.Course;
import com.todayit.course.entity.CourseStatus;
import com.todayit.course.entity.CourseTransport;
import com.todayit.course.entity.Theme;
import com.todayit.course.exception.CourseErrorCode;
import com.todayit.course.exception.CourseException;
import com.todayit.course.repository.CourseRepository;
import com.todayit.course.service.command.CreateCourseCommand;
import com.todayit.course.service.model.CreateCourseResult;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("코스 생성 서비스")
class CourseCommandServiceTest {
  @Mock private CourseRepository courses;
  @InjectMocks private CourseCommandService service;

  @Test
  @DisplayName("직접 생성은 AI 설명이나 확정 상태를 만들지 않는다")
  void createsDraftWithoutGeneratedContent() {
    // given
    when(courses.findThemesByNames(List.of("감성적"))).thenReturn(List.of(mock(Theme.class)));
    when(courses.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    CreateCourseResult result = service.createCourse("member-1", command(), List.of(7));

    // then
    assertThat(result.status()).isEqualTo(CourseStatus.DRAFT);
    assertThat(result.title()).isEqualTo("코스");
    assertThat(result.courseSummary()).isNull();
    assertThat(result.createdAt()).isNotNull();
  }

  @Test
  @DisplayName("지원하지 않는 콘셉트는 저장 전에 거부한다")
  void rejectsUnknownTheme() {
    // given
    when(courses.findThemesByNames(List.of("감성적"))).thenReturn(List.of());

    // when
    CourseException exception =
        assertThrows(
            CourseException.class, () -> service.createCourse("member-1", command(), List.of(7)));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(CourseErrorCode.UNSUPPORTED_THEME);
    verify(courses, never()).save(any());
  }

  private CreateCourseCommand command() {
    return new CreateCourseCommand(
        "코스",
        "11",
        "110",
        List.of("감성적"),
        LocalDateTime.of(2026, 12, 1, 10, 0),
        LocalDateTime.of(2026, 12, 1, 14, 0),
        CourseTransport.WALKING);
  }
}
