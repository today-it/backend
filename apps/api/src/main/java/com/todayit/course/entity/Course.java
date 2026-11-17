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

  @Column(name = "duration_minutes")
  private Integer durationMinutes;

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
   * @param endAt 방문 종료 시각, 가용시간 입력 시 null
   * @param durationMinutes 가용시간(분), 종료 시각 입력 시 null
   * @param regionIds 검증된 지역 식별자 목록
   * @param selectedThemes 검증된 콘셉트 목록
   * @param createdAt 서버 생성 시각
   * @return 장소와 설명이 없는 비공개 DRAFT
   */
  public static Course createDraft(
      String memberId,
      String title,
      CourseTransport transport,
      LocalDateTime startAt,
      LocalDateTime endAt,
      Integer durationMinutes,
      List<Integer> regionIds,
      List<Theme> selectedThemes,
      LocalDateTime createdAt) {
    Course course = new Course();
    course.memberId = memberId;
    course.title = title;
    course.transport = transport;
    course.startAt = startAt;
    course.endAt = endAt;
    course.durationMinutes = durationMinutes;
    course.status = CourseStatus.DRAFT;
    course.visibility = CourseVisibility.PRIVATE;
    course.createdAt = createdAt;
    regionIds.forEach(id -> course.regions.add(new RegionCourse(course, id)));
    selectedThemes.forEach(theme -> course.themes.add(new CourseTheme(course, theme)));
    return course;
  }

  public CourseStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
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
