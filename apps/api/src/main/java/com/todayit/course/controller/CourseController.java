package com.todayit.course.controller;

import com.todayit.common.response.CommonResponse;
import com.todayit.course.controller.docs.CourseApiDocs;
import com.todayit.course.dto.request.CreateCourseRequest;
import com.todayit.course.dto.response.CreateCourseResponse;
import com.todayit.course.service.CourseFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
   * 코스 생성 조정 서비스를 받습니다.
   *
   * @param courseFacade 코스 생성 Facade
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
}
