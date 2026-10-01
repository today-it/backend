package com.todayit.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.todayit.place.entity.Category;
import com.todayit.place.entity.Place;
import com.todayit.place.repository.PlaceRepository;
import com.todayit.place.service.model.PlaceListResult;
import com.todayit.place.service.model.PlaceResult;
import com.todayit.place.service.model.PlaceSort;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/** 장소 목록 조회 Service의 업무 결과를 검증합니다. */
@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

  @Mock private PlaceRepository placeRepository;

  @Mock private Place place;

  /** 장소를 인기순으로 조회하고 서비스 결과로 변환하는지 검증합니다. */
  @Test
  @DisplayName("장소 목록을 인기순으로 조회하고 페이지 정보와 함께 반환한다")
  void findsPlacesByPopularity() {
    // Given
    Place.PlaceSnapshot snapshot =
        new Place.PlaceSnapshot(
            1, "오늘의 식당", new BigDecimal("37.5"), new BigDecimal("127.0"), Category.RESTAURANT, 15);
    when(place.getSnapshot()).thenReturn(snapshot);
    when(placeRepository.findByIsActiveTrueAndIsDeletedFalse(
            argThat(
                pageable ->
                    pageable.equals(
                        PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "viewCount"))))))
        .thenReturn(new PageImpl<>(List.of(place), PageRequest.of(1, 2), 3));
    PlaceService placeService = new PlaceService(placeRepository);

    // When
    PlaceListResult result = placeService.findPlaces(1, 2, PlaceSort.POPULAR);

    // Then
    assertThat(result.content())
        .containsExactly(
            new PlaceResult(
                1,
                "오늘의 식당",
                new BigDecimal("37.5"),
                new BigDecimal("127.0"),
                Category.RESTAURANT,
                15));
    assertThat(result.page()).isEqualTo(1);
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.totalElements()).isEqualTo(3);
    assertThat(result.totalPages()).isEqualTo(2);
    verify(placeRepository)
        .findByIsActiveTrueAndIsDeletedFalse(
            PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "viewCount")));
  }
}
