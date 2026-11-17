package com.todayit.course.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 회원이 만든 코스의 기본 정보와 상태를 나타냅니다. */
@Entity
@Table(name = "course")
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "course_id", nullable = false)
  private int courseId;

  @Column(name = "member_id", nullable = false, length = 36)
  private String memberId;

  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @Column(name = "course_summary", length = 500)
  private String courseSummary;

  @Enumerated(EnumType.STRING)
  @Column(name = "transport", nullable = false, length = 30)
  private CourseTransport transport;

  @Column(name = "start_at", nullable = false)
  private LocalDateTime startAt;

  @Column(name = "end_at")
  private LocalDateTime endAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private CourseStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "visibility", nullable = false, length = 20)
  private CourseVisibility visibility;

  @Column(name = "view_count", nullable = false)
  private int viewCount;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CoursePlace> coursePlaces = new ArrayList<>();

  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RegionCourse> regions = new ArrayList<>();

  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CourseTheme> themes = new ArrayList<>();

  protected Course() {}

  /**
   * 입력 조건으로 비공개 임시 코스를 생성합니다.
   *
   * @param memberId 작성자 식별자
   * @param title 코스명
   * @param transport 이동수단
   * @param startAt 방문 시작 시각
   * @param endAt 방문 종료 시각, 미입력 시 null
   * @param selectedThemes 검증된 콘셉트 목록
   * @param createdAt 서버 생성 시각
   * @return 장소와 지역이 없는 비공개 DRAFT
   */
  public static Course createDraft(
      String memberId,
      String title,
      CourseTransport transport,
      LocalDateTime startAt,
      LocalDateTime endAt,
      List<Theme> selectedThemes,
      LocalDateTime createdAt) {
    Course course = new Course();
    course.memberId = memberId;
    course.title = title;
    course.transport = transport;
    course.startAt = startAt;
    course.endAt = endAt;
    course.status = CourseStatus.DRAFT;
    course.visibility = CourseVisibility.PRIVATE;
    course.createdAt = createdAt;
    selectedThemes.forEach(theme -> course.themes.add(new CourseTheme(course, theme)));
    return course;
  }

  /**
   * 특정 회원이 이 코스를 조회할 권한이 있는지 확인합니다. 공개 상태이면서 활성 상태인 코스는 누구나 조회할 수 있으며, 비공개 코스나 DRAFT 코스는 작성자 본인만
   * 조회할 수 있습니다.
   *
   * @param accessorMemberId 조회를 시도하는 회원 식별자, 비회원이면 null
   * @return 조회 가능 여부
   */
  public boolean canAccess(String accessorMemberId) {
    if (deletedAt != null || status == CourseStatus.DELETED) {
      return false;
    }
    if (visibility == CourseVisibility.PUBLIC && status == CourseStatus.ACTIVE) {
      return true;
    }
    return accessorMemberId != null && memberId.equals(accessorMemberId);
  }

  /**
   * 특정 회원이 이 코스의 작성자인지 확인합니다.
   *
   * @param accessorMemberId 확인할 회원 식별자, 비회원이면 null
   * @return 작성자 여부
   */
  public boolean isWriter(String accessorMemberId) {
    return accessorMemberId != null && memberId.equals(accessorMemberId);
  }

  /**
   * 코스 식별자를 반환합니다.
   *
   * @return 코스 식별자
   */
  public int getCourseId() {
    return courseId;
  }

  /**
   * 작성자 회원 식별자를 반환합니다.
   *
   * @return 작성자 회원 식별자
   */
  public String getMemberId() {
    return memberId;
  }

  /**
   * 코스명을 반환합니다.
   *
   * @return 코스명
   */
  public String getTitle() {
    return title;
  }

  /**
   * 코스 요약을 반환합니다.
   *
   * @return 코스 요약, 없으면 null
   */
  public String getCourseSummary() {
    return courseSummary;
  }

  /**
   * 이동수단을 반환합니다.
   *
   * @return 이동수단
   */
  public CourseTransport getTransport() {
    return transport;
  }

  /**
   * 시작 시각을 반환합니다.
   *
   * @return 시작 시각
   */
  public LocalDateTime getStartAt() {
    return startAt;
  }

  /**
   * 종료 시각을 반환합니다.
   *
   * @return 종료 시각, 없으면 null
   */
  public LocalDateTime getEndAt() {
    return endAt;
  }

  /**
   * 상태를 반환합니다.
   *
   * @return 상태
   */
  public CourseStatus getStatus() {
    return status;
  }

  /**
   * 공개 범위를 반환합니다.
   *
   * @return 공개 범위
   */
  public CourseVisibility getVisibility() {
    return visibility;
  }

  /**
   * 조회수를 반환합니다.
   *
   * @return 조회수
   */
  public int getViewCount() {
    return viewCount;
  }

  /**
   * 등록 일시를 반환합니다.
   *
   * @return 등록 일시
   */
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * 수정 일시를 반환합니다.
   *
   * @return 수정 일시, 없으면 null
   */
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  /**
   * 삭제 일시를 반환합니다.
   *
   * @return 삭제 일시, 없으면 null
   */
  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }

  /**
   * 코스에 포함된 지역 관계 목록을 불변 리스트로 반환합니다.
   *
   * @return 지역 관계 목록
   */
  public List<RegionCourse> getRegions() {
    return Collections.unmodifiableList(regions);
  }

  /**
   * 코스에 연결된 테마 관계 목록을 불변 리스트로 반환합니다.
   *
   * @return 테마 관계 목록
   */
  public List<CourseTheme> getThemes() {
    return Collections.unmodifiableList(themes);
  }

  /**
   * 코스에 포함된 장소 목록을 불변 리스트로 반환합니다.
   *
   * @return 장소 목록
   */
  public List<CoursePlace> getCoursePlaces() {
    return Collections.unmodifiableList(coursePlaces);
  }

  /**
   * 코스에 장소를 추가합니다.
   *
   * @param coursePlace 추가할 코스 장소 엔티티
   */
  public void addCoursePlace(CoursePlace coursePlace) {
    this.coursePlaces.add(coursePlace);
  }

  /**
   * 새 장소를 추가하기 위해 순서를 뒤로 밀어야 하는 대상 목록을 음수 임시 순서로 변경합니다.
   *
   * @param targetOrder 추가하려는 순서
   * @return 임시 순서로 변경된 장소 목록
   */
  public List<CoursePlace> shiftOrdersToNegative(int targetOrder) {
    int maxOrder = coursePlaces.stream().mapToInt(CoursePlace::getOrder).max().orElse(0);
    if (targetOrder > maxOrder) {
      return List.of();
    }
    int assignedOrder = Math.max(1, targetOrder);
    List<CoursePlace> targets =
        coursePlaces.stream().filter(cp -> cp.getOrder() >= assignedOrder).toList();
    for (CoursePlace cp : targets) {
      cp.setOrder(-cp.getOrder());
    }
    return targets;
  }

  /**
   * 음수 임시 순서로 변경된 장소들의 순서를 1씩 밀린 양수로 복원합니다.
   *
   * @param shiftedPlaces 임시 음수 순서로 변경된 장소 목록
   */
  public void restoreShiftedOrders(List<CoursePlace> shiftedPlaces) {
    for (CoursePlace cp : shiftedPlaces) {
      cp.setOrder(-cp.getOrder() + 1);
    }
  }

  /**
   * 코스에 새 장소를 추가합니다.
   *
   * @param placeId 추가할 장소 식별자
   * @param targetOrder 지정할 방문 순서, null이면 맨 뒤에 추가
   * @param stayMinute 체류 시간(분)
   * @return 생성된 CoursePlace 엔티티
   */
  public CoursePlace addPlace(int placeId, Integer targetOrder, int stayMinute) {
    int maxOrder = coursePlaces.stream().mapToInt(CoursePlace::getOrder).max().orElse(0);
    int assignedOrder =
        (targetOrder == null || targetOrder > maxOrder) ? maxOrder + 1 : Math.max(1, targetOrder);
    CoursePlace newCoursePlace = new CoursePlace(this, placeId, assignedOrder, stayMinute);
    this.coursePlaces.add(newCoursePlace);
    return newCoursePlace;
  }

  /**
   * 코스에 지역 관계를 추가합니다.
   *
   * @param regionId 연결할 지역 식별자
   */
  public void addRegion(int regionId) {
    this.regions.add(new RegionCourse(this, regionId));
  }

  /**
   * 코스의 조회 정보를 반환합니다.
   *
   * @return 코스 조회 정보
   */
  public CourseSnapshot getSnapshot() {
    return new CourseSnapshot(courseId, title, courseSummary, transport, startAt, endAt, viewCount);
  }

  /**
   * 코스 조회에 필요한 정보를 담습니다.
   *
   * @param courseId 코스 식별자
   * @param title 코스명
   * @param courseSummary 코스 요약
   * @param transport 이동수단
   * @param startAt 코스 시작 시간
   * @param endAt 코스 종료 시간
   * @param viewCount 조회수
   */
  public record CourseSnapshot(
      int courseId,
      String title,
      String courseSummary,
      CourseTransport transport,
      LocalDateTime startAt,
      LocalDateTime endAt,
      int viewCount) {}
}
