# Module-Main-Server-Dashboard / 메인서버-대시보드

## API / API
- GET /api/dashboard [back/main-server/src/main/java/com/example/mainserver/dashboard/controller/DashboardController.java](back/main-server/src/main/java/com/example/mainserver/dashboard/controller/DashboardController.java#L21)

## Key Classes / 주요 클래스
- `DashboardController`: API 엔드포인트. [back/main-server/src/main/java/com/example/mainserver/dashboard/controller/DashboardController.java](back/main-server/src/main/java/com/example/mainserver/dashboard/controller/DashboardController.java#L17)
- `DashboardService`: 최근 한 달 Top N 계산. [back/main-server/src/main/java/com/example/mainserver/dashboard/application/DashboardService.java](back/main-server/src/main/java/com/example/mainserver/dashboard/application/DashboardService.java#L21)
- `DashboardRepository`: 집계 쿼리. [back/main-server/src/main/java/com/example/mainserver/dashboard/infrastructure/DashboardRepository.java](back/main-server/src/main/java/com/example/mainserver/dashboard/infrastructure/DashboardRepository.java#L14)
- DTOs: [back/main-server/src/main/java/com/example/mainserver/dashboard/controller/dto/RankByAnalyticsInLastMonthResponseDto.java](back/main-server/src/main/java/com/example/mainserver/dashboard/controller/dto/RankByAnalyticsInLastMonthResponseDto.java#L13)
