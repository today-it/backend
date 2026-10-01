INSERT INTO place (
    name,
    latitude,
    longitude,
    category,
    view_count,
    is_active,
    is_deleted
)
SELECT
    seed.name,
    seed.latitude,
    seed.longitude,
    seed.category,
    seed.view_count,
    seed.is_active,
    seed.is_deleted
FROM (
    VALUES
        ('오늘의 식당', 37.57000000, 126.98500000, 'RESTAURANT', 120, TRUE, FALSE),
        ('종로 카페거리', 37.57200000, 126.98300000, 'CAFE_DESSERT', 95, TRUE, FALSE),
        ('달빛 와인바', 37.56900000, 126.99100000, 'BAR', 80, TRUE, FALSE),
        ('서울 전시 공간', 37.57700000, 126.98100000, 'EXHIBITION_CULTURE', 75, TRUE, FALSE),
        ('시네마 종로', 37.57000000, 126.97800000, 'MOVIE_PERFORMANCE', 65, TRUE, FALSE),
        ('도예 원데이 클래스', 37.57900000, 126.98900000, 'EXPERIENCE_WORKSHOP', 55, TRUE, FALSE),
        ('도심 보드게임 라운지', 37.56800000, 126.98200000, 'PLAY_ACTIVITY', 45, TRUE, FALSE),
        ('북악산 산책로', 37.59200000, 126.98100000, 'NATURE_WALK', 40, TRUE, FALSE),
        ('인사동 소품 상점', 37.57400000, 126.98500000, 'SHOPPING', 35, TRUE, FALSE),
        ('남산 전망대', 37.55100000, 126.98800000, 'LANDMARK_VIEW', 150, TRUE, FALSE),
        ('도심 스파', 37.56300000, 126.98900000, 'RELAXATION_SPA', 30, TRUE, FALSE),
        ('운영 중단 장소', 37.56500000, 126.98000000, 'RESTAURANT', 10, FALSE, FALSE),
        ('삭제된 장소', 37.56600000, 126.98100000, 'CAFE_DESSERT', 5, TRUE, TRUE)
) AS seed(name, latitude, longitude, category, view_count, is_active, is_deleted)
WHERE NOT EXISTS (
    SELECT 1
    FROM place
    WHERE place.name = seed.name
);
