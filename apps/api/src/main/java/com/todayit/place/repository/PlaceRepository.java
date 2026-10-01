package com.todayit.place.repository;

import com.todayit.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

/** 장소 정보를 조회하고 저장하는 JPA Repository입니다. */
public interface PlaceRepository extends JpaRepository<Place, Integer> {}
