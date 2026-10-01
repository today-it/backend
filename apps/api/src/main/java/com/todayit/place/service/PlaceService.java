package com.todayit.place.service;

import com.todayit.place.entity.Place;
import com.todayit.place.entity.PlaceImage;
import com.todayit.place.exception.PlaceNotFoundException;
import com.todayit.place.repository.PlaceImageRepository;
import com.todayit.place.repository.PlaceRepository;
import com.todayit.place.service.model.PlaceImageListResult;
import com.todayit.place.service.model.PlaceImageResult;
import com.todayit.place.service.model.PlaceListResult;
import com.todayit.place.service.model.PlaceLocationResult;
import com.todayit.place.service.model.PlaceResult;
import com.todayit.place.service.model.PlaceSort;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/** 장소 관련 업무를 처리하는 Service입니다. */
@Service
public class PlaceService {

  private final PlaceRepository placeRepository;
  private final PlaceImageRepository placeImageRepository;

  /**
   * 장소 정보를 조회하고 저장할 Repository를 받습니다.
   *
   * @param placeRepository 장소 Repository
   */
  public PlaceService(PlaceRepository placeRepository, PlaceImageRepository placeImageRepository) {
    this.placeRepository = placeRepository;
    this.placeImageRepository = placeImageRepository;
  }

  /**
   * 활성화되고 삭제되지 않은 장소를 페이지 단위로 조회합니다.
   *
   * @param page 페이지 번호
   * @param size 페이지 크기
   * @param sort 정렬 기준
   * @return 장소 목록과 페이지 정보
   */
  public PlaceListResult findPlaces(int page, int size, PlaceSort sort) {
    Sort order =
        switch (sort) {
          case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
          case POPULAR -> Sort.by(Sort.Direction.DESC, "viewCount");
        };

    Page<Place> placePage =
        placeRepository.findByIsActiveTrueAndIsDeletedFalse(PageRequest.of(page, size, order));
    List<PlaceResult> content = placePage.map(this::toResult).getContent();

    return new PlaceListResult(
        content,
        placePage.getNumber(),
        placePage.getSize(),
        placePage.getTotalElements(),
        placePage.getTotalPages());
  }

  /**
   * 활성화되고 삭제되지 않은 장소의 지도 정보를 조회합니다.
   *
   * @param placeId 장소 식별자
   * @return 장소 식별자, 좌표와 주소
   * @throws PlaceNotFoundException 장소가 없거나 비활성·삭제 상태일 때
   */
  public PlaceLocationResult findPlaceLocation(int placeId) {

    Place place =
        placeRepository
            .findByPlaceIdAndIsActiveTrueAndIsDeletedFalse(placeId)
            .orElseThrow(PlaceNotFoundException::new);
    Place.PlaceSnapshot snapshot = place.getSnapshot();

    return new PlaceLocationResult(
        snapshot.placeId(), snapshot.latitude(), snapshot.longitude(), snapshot.address());
  }

  /**
   * 활성화되고 삭제되지 않은 장소의 사진을 페이지 단위로 조회합니다.
   *
   * @param placeId 장소 식별자
   * @param page 페이지 번호
   * @param size 페이지 크기
   * @return 장소 사진 목록과 페이지 정보
   * @throws PlaceNotFoundException 장소가 없거나 비활성·삭제 상태일 때
   */
  public PlaceImageListResult findPlaceImages(int placeId, int page, int size) {

    placeRepository
        .findByPlaceIdAndIsActiveTrueAndIsDeletedFalse(placeId)
        .orElseThrow(PlaceNotFoundException::new);

    Page<PlaceImage> imagePage =
        placeImageRepository.findByPlace_PlaceId(placeId, PageRequest.of(page, size));

    return new PlaceImageListResult(
        imagePage.getContent().stream()
            .map(image -> new PlaceImageResult(image.getPlaceImageId(), image.getImageUrl()))
            .toList(),
        imagePage.getNumber(),
        imagePage.getSize(),
        imagePage.getTotalElements());
  }

  /**
   * 장소 Entity를 목록 조회 결과로 변환합니다.
   *
   * @param place 장소 Entity
   * @return 장소 조회 결과
   */
  private PlaceResult toResult(Place place) {
    return PlaceResult.from(place.getSnapshot());
  }
}
