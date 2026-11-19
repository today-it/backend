package com.todayit.course.service;

import com.todayit.course.entity.Course;
import com.todayit.course.entity.CoursePlace;
import com.todayit.course.entity.CourseStatus;
import com.todayit.course.entity.CourseVisibility;
import com.todayit.course.entity.Theme;
import com.todayit.course.exception.CourseErrorCode;
import com.todayit.course.exception.CourseException;
import com.todayit.course.exception.CourseNotFoundException;
import com.todayit.course.exception.CoursePlaceNotFoundException;
import com.todayit.course.exception.InvalidCoursePlaceOrderException;
import com.todayit.course.repository.CourseRepository;
import com.todayit.course.service.command.AddCoursePlaceCommand;
import com.todayit.course.service.command.CreateCourseCommand;
import com.todayit.course.service.command.UpdateCoursePlaceOrdersCommand;
import com.todayit.course.service.model.AddCoursePlaceEntities;
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
   * @return 생성 결과
   * @throws CourseException 지원하지 않는 콘셉트가 포함된 경우
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
   * 코스 행을 잠근 뒤 새 장소를 추가하고 저장합니다.
   *
   * @param memberId 인증된 작성자 식별자
   * @param command 코스 장소 추가 조건
   * @return 추가된 장소 및 전체 장소 엔티티 묶음
   * @throws CourseNotFoundException 코스가 없거나 작성자가 아닌 경우
   */
  @Transactional
  public AddCoursePlaceEntities addPlace(String memberId, AddCoursePlaceCommand command) {
    Course course =
        courseRepository
            .findByIdAndMemberIdForUpdate(command.courseId(), memberId)
            .orElseThrow(CourseNotFoundException::new);
    if (!course.isWriter(memberId)
        || course.getDeletedAt() != null
        || course.getStatus() == CourseStatus.DELETED) {
      throw new CourseNotFoundException();
    }

    if (command.order() != null) {
      List<CoursePlace> shiftedPlaces = course.shiftOrdersToNegative(command.order());
      if (!shiftedPlaces.isEmpty()) {
        courseRepository.flush();
        course.restoreShiftedOrders(shiftedPlaces);
      }
    }

    CoursePlace coursePlace =
        course.addPlace(command.placeId(), command.order(), command.stayMinute());
    Course savedCourse = courseRepository.saveAndFlush(course);
    CoursePlace persistedPlace =
        savedCourse.getCoursePlaces().stream()
            .filter(
                cp ->
                    cp.getPlaceId() == coursePlace.getPlaceId()
                        && cp.getOrder() == coursePlace.getOrder())
            .findFirst()
            .orElse(coursePlace);

    return new AddCoursePlaceEntities(persistedPlace, savedCourse.getCoursePlaces());
  }

  /**
   * 코스 행을 잠근 뒤 장소들의 방문 순서를 일괄 변경합니다.
   *
   * @param memberId 인증된 작성자 식별자
   * @param command 순서 변경 조건
   * @return 변경된 전체 코스 장소 목록
   * @throws CourseNotFoundException 코스가 없거나 작성자가 아닌 경우
   * @throws InvalidCoursePlaceOrderException 전달된 장소 목록이 유효하지 않은 경우
   */
  @Transactional
  public List<CoursePlace> updatePlaceOrders(
      String memberId, UpdateCoursePlaceOrdersCommand command) {
    Course course =
        courseRepository
            .findByIdAndMemberIdForUpdate(command.courseId(), memberId)
            .orElseThrow(CourseNotFoundException::new);
    if (!course.isWriter(memberId)
        || course.getDeletedAt() != null
        || course.getStatus() == CourseStatus.DELETED) {
      throw new CourseNotFoundException();
    }
    course.reorderPlaces(command.coursePlaceIds());
    courseRepository.flush();
    course.confirmReorder();
    Course savedCourse = courseRepository.saveAndFlush(course);
    return savedCourse.getCoursePlaces();
  }

  /**
   * 코스 행을 잠근 뒤 장소들의 방문 순서를 일괄 변경합니다 (호환용 별칭).
   *
   * @param memberId 인증된 작성자 식별자
   * @param command 순서 변경 조건
   * @return 변경된 전체 코스 장소 목록
   */
  @Transactional
  public List<CoursePlace> reorderPlaces(String memberId, UpdateCoursePlaceOrdersCommand command) {
    return updatePlaceOrders(memberId, command);
  }

  /**
   * 코스 행을 잠근 뒤 특정 장소를 삭제하고 남은 장소들의 순서를 재조정합니다.
   *
   * @param memberId 인증된 작성자 식별자
   * @param courseId 코스 식별자
   * @param coursePlaceId 삭제할 코스 장소 식별자
   * @return 삭제 후 재정렬된 남은 코스 장소 목록
   * @throws CourseNotFoundException 코스가 없거나 작성자가 아닌 경우, 또는 이미 삭제된 코스인 경우
   * @throws CoursePlaceNotFoundException 코스 내에 해당 장소가 없는 경우
   */
  @Transactional
  public List<CoursePlace> removePlace(String memberId, int courseId, int coursePlaceId) {
    Course course =
        courseRepository
            .findByIdAndMemberIdForUpdate(courseId, memberId)
            .orElseThrow(CourseNotFoundException::new);
    if (!course.isWriter(memberId)
        || course.getDeletedAt() != null
        || course.getStatus() == CourseStatus.DELETED) {
      throw new CourseNotFoundException();
    }
    List<CoursePlace> subsequentPlaces = course.shiftOrdersToNegativeForRemoval(coursePlaceId);
    course.removeCoursePlace(coursePlaceId);
    if (!subsequentPlaces.isEmpty()) {
      courseRepository.flush();
      course.restoreShiftedOrdersForRemoval(subsequentPlaces);
    }
    Course savedCourse = courseRepository.saveAndFlush(course);
    return savedCourse.getCoursePlaces();
  }

  /**
   * 코스 행을 잠근 뒤 작성자 확인 후 코스와 관련 연관 데이터를 데이터베이스에서 영구 삭제합니다.
   *
   * @param memberId 인증된 작성자 식별자
   * @param courseId 삭제할 코스 식별자
   * @throws CourseNotFoundException 코스가 없거나 작성자가 아닌 경우, 또는 이미 삭제된 코스인 경우
   */
  @Transactional
  public void deleteCourse(String memberId, int courseId) {
    Course course =
        courseRepository
            .findByIdAndMemberIdForUpdate(courseId, memberId)
            .orElseThrow(CourseNotFoundException::new);
    if (!course.isWriter(memberId)
        || course.getDeletedAt() != null
        || course.getStatus() == CourseStatus.DELETED) {
      throw new CourseNotFoundException();
    }
    courseRepository.delete(course);
    courseRepository.flush();
  }

  /**
   * 확정된 원본 코스를 복사하여 새로운 DRAFT 코스를 생성합니다.
   *
   * @param memberId 인증된 요청 회원 식별자
   * @param courseId 복사할 원본 코스 식별자
   * @return 생성된 DRAFT 코스 엔티티
   * @throws CourseNotFoundException 코스가 없거나 삭제되었거나 타인의 비공개 코스인 경우
   * @throws CourseException DRAFT 상태의 코스를 복사하려는 경우
   */
  @Transactional
  public Course copyCourse(String memberId, int courseId) {
    Course sourceCourse =
        courseRepository
            .findByIdWithPlaces(courseId)
            .filter(c -> c.getDeletedAt() == null && c.getStatus() != CourseStatus.DELETED)
            .orElseThrow(CourseNotFoundException::new);

    // 타인의 비공개 코스는 존재 여부를 노출하지 않기 위해 404 처리
    if (sourceCourse.getVisibility() == CourseVisibility.PRIVATE
        && !sourceCourse.isWriter(memberId)) {
      throw new CourseNotFoundException();
    }

    // DRAFT 상태의 코스는 복사 대상에서 제외 (본인 DRAFT 포함)
    if (sourceCourse.getStatus() == CourseStatus.DRAFT) {
      throw new CourseException(CourseErrorCode.CANNOT_COPY_DRAFT);
    }

    if (!sourceCourse.canCopy(memberId)) {
      throw new CourseNotFoundException();
    }

    Course copiedDraft = Course.createCopyDraft(memberId, sourceCourse, LocalDateTime.now());
    return courseRepository.save(copiedDraft);
  }
}
