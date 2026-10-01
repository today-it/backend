package com.todayit.place.repository;

import com.todayit.place.entity.PlaceScrap;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** 장소 스크랩 정보를 조회하고 저장하는 JPA Repository입니다. */
public interface PlaceScrapRepository extends JpaRepository<PlaceScrap, Integer> {

  /**
   * 회원의 장소 스크랩을 조회합니다.
   *
   * @param memberId 회원 식별자
   * @param placeId 장소 식별자
   * @return 회원의 장소 스크랩
   */
  Optional<PlaceScrap> findByMemberIdAndPlacePlaceId(String memberId, int placeId);

  /**
   * 장소의 활성 스크랩 수를 조회합니다.
   *
   * @param placeId 장소 식별자
   * @return 활성 스크랩 수
   */
  @Query(
      "select count(scrap) from PlaceScrap scrap "
          + "where scrap.place.placeId = :placeId and scrap.isDeleted = false")
  long countActiveByPlaceId(@Param("placeId") int placeId);
}
