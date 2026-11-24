package com.todayit.course.repository;

import com.todayit.course.entity.CourseMemberLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** 코스 좋아요 정보를 조회하고 저장하는 JPA Repository입니다. */
public interface CourseMemberLikeRepository extends JpaRepository<CourseMemberLike, Integer> {

  /**
   * 회원의 코스 좋아요 여부를 확인합니다.
   *
   * @param memberId 회원 식별자
   * @param courseId 코스 식별자
   * @return 좋아요 존재 여부
   */
  boolean existsByMemberIdAndCourseCourseId(String memberId, int courseId);

  /**
   * 회원의 코스 좋아요를 생성합니다.
   *
   * @param memberId 회원 식별자
   * @param courseId 코스 식별자
   * @return 반영된 좋아요 수
   */
  @Modifying
  @Query(
      value =
          "insert into course_member_like (member_id, course_id, created_at) "
              + "values (:memberId, :courseId, current_timestamp) "
              + "on conflict (member_id, course_id) do nothing",
      nativeQuery = true)
  int insertIfAbsent(@Param("memberId") String memberId, @Param("courseId") int courseId);

  /**
   * 코스의 좋아요 수를 조회합니다.
   *
   * @param courseId 코스 식별자
   * @return 코스 좋아요 수
   */
  @Query(
      "select count(memberLike) from CourseMemberLike memberLike "
          + "where memberLike.course.courseId = :courseId")
  long countByCourseId(@Param("courseId") int courseId);
}
