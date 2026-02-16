# Module-Cache / 캐시 모듈

## Purpose / 목적
Redis 기반 캐시로 차량/주행 로그 필터링 결과를 저장합니다.

## Components / 구성 요소
- `CacheConfig`: Redis CacheManager와 TTL 구성. [back/main-server/src/main/java/com/example/mainserver/cache/CacheConfig.java](back/main-server/src/main/java/com/example/mainserver/cache/CacheConfig.java#L25)
- `CarFilterCache`: 차량 검색 결과 캐싱. [back/main-server/src/main/java/com/example/mainserver/cache/CarFilterCache.java](back/main-server/src/main/java/com/example/mainserver/cache/CarFilterCache.java#L15)
- `DriveLogFilterCache`: 주행 로그 필터 캐싱. [back/main-server/src/main/java/com/example/mainserver/cache/DriveLogFilterCache.java](back/main-server/src/main/java/com/example/mainserver/cache/DriveLogFilterCache.java#L15)

## Notes / 비고
Cache keys include filter parameters and pagination to avoid collisions.
필터/페이징 값을 키에 포함해 충돌을 방지합니다.
