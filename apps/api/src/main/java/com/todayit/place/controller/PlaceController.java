package com.todayit.place.controller;

import com.todayit.place.service.PlaceService;
import org.springframework.web.bind.annotation.RestController;

/** 장소 관련 HTTP 요청을 처리하는 Controller입니다. */
@RestController
public class PlaceController {

  private final PlaceService placeService;

  /**
   * 장소 기능을 처리할 서비스를 받습니다.
   *
   * @param placeService 장소 Service
   */
  public PlaceController(PlaceService placeService) {
    this.placeService = placeService;
  }
}
