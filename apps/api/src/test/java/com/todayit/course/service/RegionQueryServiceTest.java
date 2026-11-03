package com.todayit.course.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.todayit.course.exception.InvalidRegionPathException;
import com.todayit.course.repository.RegionRepository;
import com.todayit.course.service.model.RegionFilter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegionQueryServiceTest {
  @Mock private RegionRepository regionRepository;

  @ParameterizedTest
  @CsvSource({"41,", ",115", "41,115", ","})
  @DisplayName("전달된 시도 또는 시군구 코드로 지역 식별자를 조회한다")
  void findsRegionsByProvidedCodes(String sidoCode, String sigunguCode) {
    // Given
    when(regionRepository.findIdsByCodes(sidoCode, sigunguCode)).thenReturn(List.of(1, 2));
    RegionQueryService service = new RegionQueryService(regionRepository);

    // When
    List<Integer> result = service.findRegionIds(sidoCode, sigunguCode);

    // Then
    assertThat(result).containsExactly(1, 2);
  }

  @ParameterizedTest
  @CsvSource({"1,", "411,", "ab,", "' ',", "'',", ",11", ",1100", ",abc", ",' '", ",''"})
  @DisplayName("잘못된 형식의 지역 코드는 조회 전에 예외를 던진다")
  void rejectsMalformedCodesBeforeQuerying(String sidoCode, String sigunguCode) {
    // Given
    RegionQueryService service = new RegionQueryService(regionRepository);

    // When & Then
    assertThatThrownBy(() -> service.findRegionIds(sidoCode, sigunguCode))
        .isInstanceOf(InvalidRegionPathException.class);
    verifyNoInteractions(regionRepository);
  }

  @Test
  @DisplayName("존재하지 않는 지역 코드를 조회하면 예외를 던진다")
  void rejectsUnknownRegion() {
    // Given
    when(regionRepository.findIdsByCodes("41", "999")).thenReturn(List.of());
    RegionQueryService service = new RegionQueryService(regionRepository);

    // When & Then
    assertThatThrownBy(() -> service.findRegionIds("41", "999"))
        .isInstanceOf(InvalidRegionPathException.class);
  }

  @Test
  @DisplayName("지역 필터 목록으로 지역 식별자들을 조회한다")
  void findsRegionsByFilterList() {
    // Given
    when(regionRepository.findIdsBySidoCode("41")).thenReturn(List.of(1, 2));
    when(regionRepository.findIdsBySidoCodeAndSigunguCodes("11", List.of("680", "650")))
        .thenReturn(List.of(10, 11));

    RegionQueryService service = new RegionQueryService(regionRepository);
    List<RegionFilter> filters =
        List.of(
            new RegionFilter("41", List.of()),
            new RegionFilter("11", List.of("680", "650")));

    // When
    List<Integer> result = service.findRegionIds(filters);

    // Then
    assertThat(result).containsExactly(1, 2, 10, 11);
  }

  @Test
  @DisplayName("지역 필터 목록에 잘못된 형식의 코드가 포함되면 예외를 던진다")
  void rejectsFilterWithMalformedCodes() {
    // Given
    RegionQueryService service = new RegionQueryService(regionRepository);

    // When & Then
    assertThatThrownBy(() -> service.findRegionIds(List.of(new RegionFilter("411", List.of()))))
        .isInstanceOf(InvalidRegionPathException.class);

    assertThatThrownBy(
            () -> service.findRegionIds(List.of(new RegionFilter("41", List.of("11")))))
        .isInstanceOf(InvalidRegionPathException.class);
    verifyNoInteractions(regionRepository);
  }

  @Test
  @DisplayName("지역 필터 목록에 존재하지 않는 시도 코드가 포함되면 예외를 던진다")
  void rejectsFilterWithUnknownRegion() {
    // Given
    when(regionRepository.findIdsBySidoCode("99")).thenReturn(List.of());
    RegionQueryService service = new RegionQueryService(regionRepository);

    // When & Then
    assertThatThrownBy(() -> service.findRegionIds(List.of(new RegionFilter("99", List.of()))))
        .isInstanceOf(InvalidRegionPathException.class);
  }

  @Test
  @DisplayName("지역 필터 목록에 존재하지 않는 시군구 코드가 포함되면 예외를 던진다")
  void rejectsFilterWithUnknownSigungu() {
    // Given
    when(regionRepository.findIdsBySidoCodeAndSigunguCodes("41", List.of("999")))
        .thenReturn(List.of());
    RegionQueryService service = new RegionQueryService(regionRepository);

    // When & Then
    assertThatThrownBy(() -> service.findRegionIds(List.of(new RegionFilter("41", List.of("999")))))
        .isInstanceOf(InvalidRegionPathException.class);
  }
}
