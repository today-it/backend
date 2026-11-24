package com.todayit.course.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.todayit.course.exception.CannotChangeDraftVisibilityException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("코스 엔티티 단위 테스트")
class CourseTest {

  @Test
  @DisplayName("확정된 코스의 공개 범위를 변경하면 공개 범위와 수정 일시가 갱신된다")
  void changesVisibilitySuccessfullyForActiveCourse() {
    // given
    Course course =
        Course.createDraft(
            "member-1",
            "내 코스",
            CourseTransport.CAR,
            LocalDateTime.of(2026, 11, 24, 10, 0),
            LocalDateTime.of(2026, 11, 24, 18, 0),
            List.of(),
            LocalDateTime.of(2026, 11, 24, 9, 0));
    ReflectionTestUtils.setField(course, "status", CourseStatus.ACTIVE);
    ReflectionTestUtils.setField(course, "visibility", CourseVisibility.PUBLIC);

    LocalDateTime updatedAt = LocalDateTime.of(2026, 11, 24, 12, 0);

    // when
    course.changeVisibility(CourseVisibility.PRIVATE, updatedAt);

    // then
    assertThat(course.getVisibility()).isEqualTo(CourseVisibility.PRIVATE);
    assertThat(course.getUpdatedAt()).isEqualTo(updatedAt);
  }

  @Test
  @DisplayName("임시저장 상태의 코스는 공개 범위를 변경할 수 없다")
  void rejectsChangingVisibilityForDraftCourse() {
    // given
    Course course =
        Course.createDraft(
            "member-1",
            "임시 코스",
            CourseTransport.CAR,
            LocalDateTime.of(2026, 11, 24, 10, 0),
            LocalDateTime.of(2026, 11, 24, 18, 0),
            List.of(),
            LocalDateTime.of(2026, 11, 24, 9, 0));
    ReflectionTestUtils.setField(course, "status", CourseStatus.DRAFT);

    LocalDateTime updatedAt = LocalDateTime.of(2026, 11, 24, 12, 0);

    // when & then
    assertThrows(
        CannotChangeDraftVisibilityException.class,
        () -> course.changeVisibility(CourseVisibility.PUBLIC, updatedAt));
  }
}
