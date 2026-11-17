package com.todayit.course.controller;

import com.todayit.common.response.CommonResponse;
import com.todayit.course.controller.docs.CourseApiDocs;
import com.todayit.course.dto.request.AddCoursePlaceRequest;
import com.todayit.course.dto.request.CreateCourseRequest;
import com.todayit.course.dto.response.AddCoursePlaceResponse;
import com.todayit.course.dto.response.CourseDetailResponse;
import com.todayit.course.dto.response.CreateCourseResponse;
import com.todayit.course.service.CourseFacade;
import com.todayit.course.service.command.AddCoursePlaceCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 코스 API 요청을 처리합니다. */
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController implements CourseApiDocs {

  private final CourseFacade courseFacade;

  /**
   * 코스 생성 및 조회 조정 서비스를 받습니다.
   *
   * @param courseFacade 코스 Facade
   */
  public CourseController(CourseFacade courseFacade) {
    this.courseFacade = courseFacade;
  }

  @Override
  @PostMapping
  public ResponseEntity<CommonResponse<CreateCourseResponse>> createCourse(
      @RequestBody CreateCourseRequest request, Authentication authentication) {
    request.validate();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            CommonResponse.success(
                CreateCourseResponse.from(
                    courseFacade.createCourse(authentication.getName(), request.toCommand()))));
  }

  @Override
  @GetMapping("/{courseId}")
  public ResponseEntity<CommonResponse<CourseDetailResponse>> getCourseDetail(
      @PathVariable int courseId, Authentication authentication) {
    String memberId = authentication == null ? null : authentication.getName();
    return ResponseEntity.ok(
        CommonResponse.success(
            CourseDetailResponse.from(courseFacade.getCourseDetail(courseId, memberId))));
  }

  @Override
  @PostMapping("/{courseId}/places")
  public ResponseEntity<CommonResponse<AddCoursePlaceResponse>> addCoursePlace(
      @PathVariable int courseId,
      @RequestBody AddCoursePlaceRequest request,
      Authentication authentication) {
    request.validate();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            CommonResponse.success(
                AddCoursePlaceResponse.from(
                    courseFacade.addPlaceToCourse(
                        authentication.getName(),
                        new AddCoursePlaceCommand(
                            courseId,
                            request.placeId(),
                            request.order(),
                            request.resolveStayMinute())))));
  }
}
