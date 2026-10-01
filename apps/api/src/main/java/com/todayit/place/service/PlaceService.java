package com.todayit.place.service;

import com.todayit.place.repository.PlaceRepository;
import org.springframework.stereotype.Service;

/** 장소 관련 업무를 처리하는 Service입니다. */
@Service
public class PlaceService {

  private final PlaceRepository placeRepository;

  /**
   * 장소 정보를 조회하고 저장할 Repository를 받습니다.
   *
   * @param placeRepository 장소 Repository
   */
  public PlaceService(PlaceRepository placeRepository) {
    this.placeRepository = placeRepository;
  }
}
