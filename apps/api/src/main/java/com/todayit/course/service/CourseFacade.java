package com.todayit.course.service;

import com.todayit.course.service.command.CreateCourseCommand;
import com.todayit.course.service.model.CreateCourseResult;
import java.util.List;
import org.springframework.stereotype.Service;

/** 행정 지역 검증과 코스 생성을 조정합니다. */
@Service
public class CourseFacade {

  private final RegionQueryService regionQueryService;
  private final CourseCommandService courseCommandService;

  /**
   * 지역 조회와 코스 저장 서비스를 받습니다.
   *
   * @param regionQueryService 지역 검증 서비스
   * @param courseCommandService 코스 저장 서비스
   */
  public CourseFacade(
      RegionQueryService regionQueryService, CourseCommandService courseCommandService) {
    this.regionQueryService = regionQueryService;
    this.courseCommandService = courseCommandService;
  }

  /**
   * 지역 코드를 확인하고 비공개 DRAFT를 생성합니다.
   *
   * @param memberId 인증된 작성자 식별자
   * @param command 코스 생성 조건
   * @return 생성 결과
   */
  public CreateCourseResult createCourse(String memberId, CreateCourseCommand command) {
    List<Integer> regionIds =
        regionQueryService.findRegionIds(command.sidoCode(), command.sigunguCode());
    return courseCommandService.createCourse(memberId, command, regionIds);
  }
}
