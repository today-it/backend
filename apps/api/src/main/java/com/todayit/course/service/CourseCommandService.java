package com.todayit.course.service;

import com.todayit.course.entity.Course;
import com.todayit.course.entity.CoursePlace;
import com.todayit.course.entity.CourseStatus;
import com.todayit.course.entity.Theme;
import com.todayit.course.exception.CourseErrorCode;
import com.todayit.course.exception.CourseException;
import com.todayit.course.exception.CourseNotFoundException;
import com.todayit.course.repository.CourseRepository;
import com.todayit.course.service.command.AddCoursePlaceCommand;
import com.todayit.course.service.command.CreateCourseCommand;
import com.todayit.course.service.model.AddCoursePlaceResult;
import com.todayit.course.service.model.CreateCourseResult;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 코스의 생성 조건과 분류 관계를 저장합니다. */
@Service
public class CourseCommandService {

  private final CourseRepository courseRepository;

  /**
   * 코스 저장소를 받습니다.
   *
   * @param courseRepository 코스 저장소
   */
  public CourseCommandService(CourseRepository courseRepository) {
    this.courseRepository = courseRepository;
  }

  /**
   * 입력 조건과 분류 관계를 함께 저장합니다.
   *
   * @param memberId 인증된 작성자 식별자
   * @param command 검증된 생성 조건
   * @return 생성한 DRAFT 정보
   * @throws CourseException 지원하지 않는 콘셉트를 지정했을 때
   */
  @Transactional
  public CreateCourseResult createCourse(String memberId, CreateCourseCommand command) {
    List<Theme> themes = courseRepository.findThemesByNames(command.themes());
    if (themes.size() != command.themes().size()) {
      throw new CourseException(CourseErrorCode.UNSUPPORTED_THEME);
    }
    Course course =
        Course.createDraft(
            memberId,
            command.title(),
            command.transport(),
            command.startAt(),
            command.endAt(),
            themes,
            LocalDateTime.now());
    return CreateCourseResult.from(courseRepository.save(course));
  }

  /**
   * 코스에 새 장소를 추가하고 저장합니다.
   *
   * @param memberId 인증된 작성자 식별자
   * @param command 코스 장소 추가 조건
   * @return 추가 결과
   * @throws CourseNotFoundException 코스가 없거나 작성자가 아닌 경우
   */
  @Transactional
  public AddCoursePlaceResult addPlace(String memberId, AddCoursePlaceCommand command) {
    Course course =
        courseRepository.findById(command.courseId()).orElseThrow(CourseNotFoundException::new);
    if (!course.isWriter(memberId)
        || course.getDeletedAt() != null
        || course.getStatus() == CourseStatus.DELETED) {
      throw new CourseNotFoundException();
    }
    if (command.order() != null) {
      List<CoursePlace> shifted = course.shiftOrdersToNegative(command.order());
      if (!shifted.isEmpty()) {
        courseRepository.flush();
        course.restoreShiftedOrders(shifted);
        courseRepository.flush();
      }
    }
    CoursePlace coursePlace =
        course.addPlace(command.placeId(), command.order(), command.stayMinute());
    Course savedCourse = courseRepository.saveAndFlush(course);
    CoursePlace persistedPlace =
        (savedCourse != null)
            ? savedCourse.getCoursePlaces().stream()
                .filter(cp -> cp.getOrder() == coursePlace.getOrder())
                .findFirst()
                .orElse(coursePlace)
            : coursePlace;
    return AddCoursePlaceResult.from(persistedPlace);
  }
}
