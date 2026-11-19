package com.todayit.course.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.todayit.course.entity.Course;
import com.todayit.course.entity.CoursePlace;
import com.todayit.course.entity.CourseTransport;
import com.todayit.course.entity.Theme;
import com.todayit.course.exception.CourseErrorCode;
import com.todayit.course.exception.CourseException;
import com.todayit.course.exception.CourseNotFoundException;
import com.todayit.course.exception.CoursePlaceNotFoundException;
import com.todayit.course.exception.InvalidCoursePlaceOrderException;
import com.todayit.course.exception.MaxPlaceDuplicationExceededException;
import com.todayit.course.repository.CourseRepository;
import com.todayit.course.service.command.AddCoursePlaceCommand;
import com.todayit.course.service.command.CreateCourseCommand;
import com.todayit.course.service.command.UpdateCoursePlaceOrdersCommand;
import com.todayit.course.service.model.AddCoursePlaceResult;
import com.todayit.course.service.model.CreateCourseResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("코스 생성 및 장소 추가 서비스")
class CourseCommandServiceTest {
  @Mock private CourseRepository courses;
  @InjectMocks private CourseCommandService service;

  @Test
  @DisplayName("코스를 생성하면 저장된 DRAFT의 courseId를 반환한다")
  void createsDraftWithoutGeneratedContent() {
    // given
    when(courses.findThemesByNames(List.of("감성적"))).thenReturn(List.of(mock(Theme.class)));
    when(courses.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    CreateCourseResult result = service.createCourse("member-1", command());

    // then
    assertThat(result.courseId()).isEqualTo(0);
  }

  @Test
  @DisplayName("지원하지 않는 콘셉트는 저장 전에 거부한다")
  void rejectsUnknownTheme() {
    // given
    when(courses.findThemesByNames(List.of("감성적"))).thenReturn(List.of());

    // when
    CourseException exception =
        assertThrows(CourseException.class, () -> service.createCourse("member-1", command()));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(CourseErrorCode.UNSUPPORTED_THEME);
    verify(courses, never()).save(any());
  }

  @Test
  @DisplayName("코스에 장소를 추가하면 저장 후 생성된 장소 정보를 반환한다")
  void addsPlaceSuccessfully() {
    // given
    Course course =
        Course.createDraft(
            "member-1",
            "코스",
            CourseTransport.WALKING,
            LocalDateTime.of(2026, 12, 1, 10, 0),
            null,
            List.of(),
            LocalDateTime.now());
    when(courses.findByIdAndMemberIdForUpdate(1, "member-1")).thenReturn(Optional.of(course));
    when(courses.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    AddCoursePlaceCommand command = new AddCoursePlaceCommand(1, 10, null, 60);
    AddCoursePlaceResult result = service.addPlace("member-1", command);

    // then
    assertThat(result.placeId()).isEqualTo(10);
    assertThat(result.order()).isEqualTo(1);
    assertThat(result.stayMinute()).isEqualTo(60);
    verify(courses).saveAndFlush(course);
  }

  @Test
  @DisplayName("동일한 장소를 2회까지 추가하는 것은 허용된다")
  void allowsAddingSamePlaceUpToTwice() {
    // given
    Course course =
        Course.createDraft(
            "member-1",
            "코스",
            CourseTransport.WALKING,
            LocalDateTime.of(2026, 12, 1, 10, 0),
            null,
            List.of(),
            LocalDateTime.now());
    course.addPlace(10, null, 60);
    when(courses.findByIdAndMemberIdForUpdate(1, "member-1")).thenReturn(Optional.of(course));
    when(courses.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    AddCoursePlaceCommand command = new AddCoursePlaceCommand(1, 10, null, 45);
    AddCoursePlaceResult result = service.addPlace("member-1", command);

    // then
    assertThat(result.placeId()).isEqualTo(10);
    assertThat(result.order()).isEqualTo(2);
    assertThat(result.stayMinute()).isEqualTo(45);
    assertThat(course.getCoursePlaces()).hasSize(2);
    verify(courses).saveAndFlush(course);
  }

  @Test
  @DisplayName("동일한 장소를 3회 추가하려 하면 MaxPlaceDuplicationExceededException을 던진다")
  void rejectsAddingSamePlaceMoreThanTwice() {
    // given
    Course course =
        Course.createDraft(
            "member-1",
            "코스",
            CourseTransport.WALKING,
            LocalDateTime.of(2026, 12, 1, 10, 0),
            null,
            List.of(),
            LocalDateTime.now());
    course.addPlace(10, null, 60);
    course.addPlace(10, null, 45);
    when(courses.findByIdAndMemberIdForUpdate(1, "member-1")).thenReturn(Optional.of(course));

    // when & then
    AddCoursePlaceCommand command = new AddCoursePlaceCommand(1, 10, null, 30);
    assertThrows(
        MaxPlaceDuplicationExceededException.class, () -> service.addPlace("member-1", command));
    verify(courses, never()).saveAndFlush(any());
  }

  @Test
  @DisplayName("중간 순서로 장소를 추가하면 기존 순서들을 밀고 저장한다")
  void shiftsOrdersWhenAddingAtMiddle() {
    // given
    Course course =
        Course.createDraft(
            "member-1",
            "코스",
            CourseTransport.WALKING,
            LocalDateTime.of(2026, 12, 1, 10, 0),
            null,
            List.of(),
            LocalDateTime.now());
    course.addPlace(10, null, 60);
    when(courses.findByIdAndMemberIdForUpdate(1, "member-1")).thenReturn(Optional.of(course));
    when(courses.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // when
    AddCoursePlaceCommand command = new AddCoursePlaceCommand(1, 20, 1, 30);
    AddCoursePlaceResult result = service.addPlace("member-1", command);

    // then
    assertThat(result.placeId()).isEqualTo(20);
    assertThat(result.order()).isEqualTo(1);
    assertThat(result.stayMinute()).isEqualTo(30);
    verify(courses, times(2)).flush();
    verify(courses).saveAndFlush(course);
  }

  @Test
  @DisplayName("존재하지 않는 코스에 장소를 추가하려 하면 404 예외를 던진다")
  void rejectsWhenCourseNotFound() {
    // given
    when(courses.findByIdAndMemberIdForUpdate(999, "member-1")).thenReturn(Optional.empty());

    // when & then
    AddCoursePlaceCommand command = new AddCoursePlaceCommand(999, 10, 1, 60);
    assertThrows(CourseNotFoundException.class, () -> service.addPlace("member-1", command));
    verify(courses, never()).saveAndFlush(any());
  }

  @Test
  @DisplayName("작성자가 아닌 회원이 코스에 장소를 추가하려 하면 404 예외를 던진다")
  void rejectsWhenNotWriter() {
    // given
    when(courses.findByIdAndMemberIdForUpdate(1, "member-2")).thenReturn(Optional.empty());

    // when & then
    AddCoursePlaceCommand command = new AddCoursePlaceCommand(1, 10, 1, 60);
    assertThrows(CourseNotFoundException.class, () -> service.addPlace("member-2", command));
    verify(courses, never()).saveAndFlush(any());
  }

  private CreateCourseCommand command() {
    return new CreateCourseCommand(
        "코스",
        List.of("감성적"),
        LocalDateTime.of(2026, 12, 1, 10, 0),
        LocalDateTime.of(2026, 12, 1, 14, 0),
        CourseTransport.WALKING);
  }

  @Test
  @DisplayName("코스 장소의 방문 순서를 일괄 변경한다")
  void reordersPlacesSuccessfully() {
    // given
    Course course =
        Course.createDraft(
            "member-1",
            "코스",
            CourseTransport.WALKING,
            LocalDateTime.of(2026, 12, 1, 10, 0),
            null,
            List.of(),
            LocalDateTime.now());
    CoursePlace cp1 = course.addPlace(10, null, 60);
    CoursePlace cp2 = course.addPlace(20, null, 30);
    ReflectionTestUtils.setField(cp1, "coursePlaceId", 101);
    ReflectionTestUtils.setField(cp2, "coursePlaceId", 102);

    when(courses.findByIdAndMemberIdForUpdate(1, "member-1")).thenReturn(Optional.of(course));

    // when
    UpdateCoursePlaceOrdersCommand command =
        new UpdateCoursePlaceOrdersCommand(1, List.of(102, 101));
    service.reorderPlaces("member-1", command);

    // then
    assertThat(cp2.getOrder()).isEqualTo(1);
    assertThat(cp1.getOrder()).isEqualTo(2);
    verify(courses).flush();
    verify(courses).saveAndFlush(course);
  }

  @Test
  @DisplayName("순서 변경 시 코스가 존재하지 않으면 404 예외를 던진다")
  void rejectsReorderWhenCourseNotFound() {
    // given
    when(courses.findByIdAndMemberIdForUpdate(999, "member-1")).thenReturn(Optional.empty());

    // when & then
    UpdateCoursePlaceOrdersCommand command = new UpdateCoursePlaceOrdersCommand(999, List.of(1, 2));
    assertThrows(CourseNotFoundException.class, () -> service.reorderPlaces("member-1", command));
    verify(courses, never()).saveAndFlush(any());
  }

  @Test
  @DisplayName("순서 변경 시 작성자가 아니면 404 예외를 던진다")
  void rejectsReorderWhenNotWriter() {
    // given
    when(courses.findByIdAndMemberIdForUpdate(1, "member-2")).thenReturn(Optional.empty());

    // when & then
    UpdateCoursePlaceOrdersCommand command = new UpdateCoursePlaceOrdersCommand(1, List.of(1, 2));
    assertThrows(CourseNotFoundException.class, () -> service.reorderPlaces("member-2", command));
    verify(courses, never()).saveAndFlush(any());
  }

  @Test
  @DisplayName("순서 변경 시 전달된 장소 ID 목록이 코스의 장소들과 일치하지 않으면 400 예외를 던진다")
  void rejectsReorderWhenPlaceIdsMismatch() {
    // given
    Course course =
        Course.createDraft(
            "member-1",
            "코스",
            CourseTransport.WALKING,
            LocalDateTime.of(2026, 12, 1, 10, 0),
            null,
            List.of(),
            LocalDateTime.now());
    CoursePlace cp1 = course.addPlace(10, null, 60);
    ReflectionTestUtils.setField(cp1, "coursePlaceId", 101);
    when(courses.findByIdAndMemberIdForUpdate(1, "member-1")).thenReturn(Optional.of(course));

    // when & then
    UpdateCoursePlaceOrdersCommand command = new UpdateCoursePlaceOrdersCommand(1, List.of(999));
    assertThrows(
        InvalidCoursePlaceOrderException.class, () -> service.reorderPlaces("member-1", command));
    verify(courses, never()).saveAndFlush(any());
  }

  @Test
  @DisplayName("장소를 삭제하면 후속 장소들의 순서를 1씩 당기고 저장한다")
  void removesPlaceAndShiftsSubsequentOrdersSuccessfully() {
    // given
    Course course =
        Course.createDraft(
            "member-1",
            "코스",
            CourseTransport.WALKING,
            LocalDateTime.of(2026, 12, 1, 10, 0),
            null,
            List.of(),
            LocalDateTime.now());
    CoursePlace cp1 = course.addPlace(10, null, 60);
    CoursePlace cp2 = course.addPlace(20, null, 30);
    CoursePlace cp3 = course.addPlace(30, null, 40);
    ReflectionTestUtils.setField(cp1, "coursePlaceId", 101);
    ReflectionTestUtils.setField(cp2, "coursePlaceId", 102);
    ReflectionTestUtils.setField(cp3, "coursePlaceId", 103);

    when(courses.findByIdAndMemberIdForUpdate(1, "member-1")).thenReturn(Optional.of(course));

    // when
    service.removePlace("member-1", 1, 102);

    // then
    assertThat(course.getCoursePlaces()).hasSize(2);
    assertThat(cp1.getOrder()).isEqualTo(1);
    assertThat(cp3.getOrder()).isEqualTo(2);
    verify(courses).flush();
    verify(courses).saveAndFlush(course);
  }

  @Test
  @DisplayName("마지막 장소를 삭제하면 순서 이동 없이 삭제 후 저장한다")
  void removesLastPlaceWithoutShifting() {
    // given
    Course course =
        Course.createDraft(
            "member-1",
            "코스",
            CourseTransport.WALKING,
            LocalDateTime.of(2026, 12, 1, 10, 0),
            null,
            List.of(),
            LocalDateTime.now());
    CoursePlace cp1 = course.addPlace(10, null, 60);
    CoursePlace cp2 = course.addPlace(20, null, 30);
    ReflectionTestUtils.setField(cp1, "coursePlaceId", 101);
    ReflectionTestUtils.setField(cp2, "coursePlaceId", 102);

    when(courses.findByIdAndMemberIdForUpdate(1, "member-1")).thenReturn(Optional.of(course));

    // when
    service.removePlace("member-1", 1, 102);

    // then
    assertThat(course.getCoursePlaces()).hasSize(1);
    assertThat(cp1.getOrder()).isEqualTo(1);
    verify(courses, never()).flush();
    verify(courses).saveAndFlush(course);
  }

  @Test
  @DisplayName("마지막 남은 단일 장소를 삭제하면 빈 DRAFT 상태가 유지된다")
  void removesOnlyPlaceLeavingEmptyDraft() {
    // given
    Course course =
        Course.createDraft(
            "member-1",
            "코스",
            CourseTransport.WALKING,
            LocalDateTime.of(2026, 12, 1, 10, 0),
            null,
            List.of(),
            LocalDateTime.now());
    CoursePlace cp1 = course.addPlace(10, null, 60);
    ReflectionTestUtils.setField(cp1, "coursePlaceId", 101);

    when(courses.findByIdAndMemberIdForUpdate(1, "member-1")).thenReturn(Optional.of(course));

    // when
    service.removePlace("member-1", 1, 101);

    // then
    assertThat(course.getCoursePlaces()).isEmpty();
    verify(courses, never()).flush();
    verify(courses).saveAndFlush(course);
  }

  @Test
  @DisplayName("장소 삭제 시 코스가 존재하지 않으면 404 예외를 던진다")
  void rejectsRemovePlaceWhenCourseNotFound() {
    // given
    when(courses.findByIdAndMemberIdForUpdate(999, "member-1")).thenReturn(Optional.empty());

    // when & then
    assertThrows(CourseNotFoundException.class, () -> service.removePlace("member-1", 999, 101));
    verify(courses, never()).saveAndFlush(any());
  }

  @Test
  @DisplayName("장소 삭제 시 코스 내에 해당 장소가 존재하지 않으면 404 CoursePlaceNotFoundException을 던진다")
  void rejectsRemovePlaceWhenPlaceNotFoundInCourse() {
    // given
    Course course =
        Course.createDraft(
            "member-1",
            "코스",
            CourseTransport.WALKING,
            LocalDateTime.of(2026, 12, 1, 10, 0),
            null,
            List.of(),
            LocalDateTime.now());
    CoursePlace cp1 = course.addPlace(10, null, 60);
    ReflectionTestUtils.setField(cp1, "coursePlaceId", 101);

    when(courses.findByIdAndMemberIdForUpdate(1, "member-1")).thenReturn(Optional.of(course));

    // when & then
    assertThrows(CoursePlaceNotFoundException.class, () -> service.removePlace("member-1", 1, 999));
    verify(courses, never()).saveAndFlush(any());
  }
}
