package com.todayit.course.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.todayit.course.entity.CourseTransport;
import com.todayit.course.exception.InvalidRegionPathException;
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
@DisplayName("코스 생성 Facade")
class CourseFacadeTest {
  @Mock private RegionQueryService regions;
  @Mock private CourseCommandService courses;
  @InjectMocks private CourseFacade facade;

  @Test
  @DisplayName("시도 코드에 해당하는 지역으로 코스를 생성한다")
  void createsUsingSido() {
    // given
    CreateCourseCommand command =
        new CreateCourseCommand(
            "코스",
            "11",
            null,
            List.of("감성적"),
            LocalDateTime.of(2026, 12, 1, 10, 0),
            LocalDateTime.of(2026, 12, 1, 14, 0),
            CourseTransport.WALKING);
    CreateCourseResult expected = new CreateCourseResult(1);
    when(regions.findRegionIds("11", null)).thenReturn(List.of(7, 8));
    when(courses.createCourse("member-1", command, List.of(7, 8))).thenReturn(expected);

    // when
    CreateCourseResult result = facade.createCourse("member-1", command);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  @DisplayName("시도·시군구 코드에 해당하는 지역으로 코스를 생성한다")
  void createsUsingSigungu() {
    // given
    CreateCourseCommand command =
        new CreateCourseCommand(
            "코스",
            "11",
            "110",
            List.of("감성적"),
            LocalDateTime.of(2026, 12, 1, 10, 0),
            LocalDateTime.of(2026, 12, 1, 14, 0),
            CourseTransport.WALKING);
    CreateCourseResult expected = new CreateCourseResult(1);
    when(regions.findRegionIds("11", "110")).thenReturn(List.of(7, 8));
    when(courses.createCourse("member-1", command, List.of(7, 8))).thenReturn(expected);

    // when
    CreateCourseResult result = facade.createCourse("member-1", command);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  @DisplayName("존재하지 않는 지역이면 코스를 저장하지 않는다")
  void rejectsUnknownRegionBeforeSaving() {
    // given
    CreateCourseCommand command =
        new CreateCourseCommand(
            "코스",
            "99",
            "999",
            List.of("감성적"),
            LocalDateTime.of(2026, 12, 1, 10, 0),
            LocalDateTime.of(2026, 12, 1, 14, 0),
            CourseTransport.WALKING);
    when(regions.findRegionIds("99", "999")).thenThrow(new InvalidRegionPathException());

    // when
    InvalidRegionPathException exception =
        assertThrows(
            InvalidRegionPathException.class, () -> facade.createCourse("member-1", command));

    // then
    assertThat(exception.getErrorCode().code()).isEqualTo("INVALID_REGION_PATH");
    verifyNoInteractions(courses);
  }
}
