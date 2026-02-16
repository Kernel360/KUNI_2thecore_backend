# API Reference / API 문서

## Base URL / 기본 주소
- Main Server: http://localhost:8080

## Admin / 관리자
- POST /api/admin/signup → Admin 가입
  - Controller: [back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java](back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java#L21)
  - Request: [back/main-server/src/main/java/com/example/mainserver/admin/controller/dto/AdminRequest.java](back/main-server/src/main/java/com/example/mainserver/admin/controller/dto/AdminRequest.java#L14)
  - Response: [back/main-server/src/main/java/com/example/mainserver/admin/controller/dto/AdminResponse.java](back/main-server/src/main/java/com/example/mainserver/admin/controller/dto/AdminResponse.java#L14)
- PUT /api/admin/{loginId} → Admin 수정
  - Controller: [back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java](back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java#L32)
- DELETE /api/admin/{loginId} → Admin 삭제
  - Controller: [back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java](back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java#L43)

## Auth / 인증
- POST /api/auth/login → 로그인
  - Controller: [back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java](back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java#L26)
  - Request: [back/main-server/src/main/java/com/example/mainserver/auth/domain/LoginRequest.java](back/main-server/src/main/java/com/example/mainserver/auth/domain/LoginRequest.java#L11)
  - Response: [back/main-server/src/main/java/com/example/mainserver/auth/domain/TokenDto.java](back/main-server/src/main/java/com/example/mainserver/auth/domain/TokenDto.java#L13)
- POST /api/auth/logout → 로그아웃
  - Controller: [back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java](back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java#L32)
- POST /api/auth/verify → 자동 로그인/토큰 확인
  - Controller: [back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java](back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java#L59)
  - Response: [back/main-server/src/main/java/com/example/mainserver/auth/domain/AutoLoginResponse.java](back/main-server/src/main/java/com/example/mainserver/auth/domain/AutoLoginResponse.java#L10)

## Car / 차량
- GET /api/cars?carNumber= → 차량 상세 조회
  - Controller: [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L26)
  - Response: [back/main-server/src/main/java/com/example/mainserver/car/controller/dto/CarDetailDto.java](back/main-server/src/main/java/com/example/mainserver/car/controller/dto/CarDetailDto.java#L14)
- GET /api/cars/statistics → 상태별 통계
  - Controller: [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L34)
  - Response: [back/main-server/src/main/java/com/example/mainserver/car/controller/dto/CarSummaryDto.java](back/main-server/src/main/java/com/example/mainserver/car/controller/dto/CarSummaryDto.java#L12)
- GET /api/cars/search → 필터 검색
  - Controller: [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L48)
  - Filter DTO: [back/main-server/src/main/java/com/example/mainserver/car/controller/dto/CarFilterRequestDto.java](back/main-server/src/main/java/com/example/mainserver/car/controller/dto/CarFilterRequestDto.java#L14)
- POST /api/cars → 차량 등록
  - Controller: [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L61)
  - Request DTO: [back/common/src/main/java/com/example/common/dto/CarRequestDto.java](back/common/src/main/java/com/example/common/dto/CarRequestDto.java#L15)
- PATCH /api/cars?carNumber= → 차량 수정
  - Controller: [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L73)
- DELETE /api/cars/{car_number} → 차량 삭제
  - Controller: [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L87)
- GET /api/cars/status?status= → 상태별 목록
  - Controller: [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L98)
- GET /api/cars/locations?status= → 위치 목록
  - Controller: [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L110)
  - Response DTO: [back/main-server/src/main/java/com/example/mainserver/car/controller/dto/CarLocationDto.java](back/main-server/src/main/java/com/example/mainserver/car/controller/dto/CarLocationDto.java#L11)

## Collector / 수집
- POST /api/logs/gps → GPS 로그 수집 (RabbitMQ 발행)
  - Controller: [back/main-server/src/main/java/com/example/mainserver/collector/controller/CollectorController.java](back/main-server/src/main/java/com/example/mainserver/collector/controller/CollectorController.java#L21)
  - Request DTO: [back/main-server/src/main/java/com/example/mainserver/collector/domain/dto/GpsLogDto.java](back/main-server/src/main/java/com/example/mainserver/collector/domain/dto/GpsLogDto.java#L20)
- POST /api/logs/gps-direct → Hub 직접 호출
  - Controller: [back/main-server/src/main/java/com/example/mainserver/collector/controller/CollectorController.java](back/main-server/src/main/java/com/example/mainserver/collector/controller/CollectorController.java#L30)

## Dashboard / 대시보드
- GET /api/dashboard → 최근 1개월 Top N
  - Controller: [back/main-server/src/main/java/com/example/mainserver/dashboard/controller/DashboardController.java](back/main-server/src/main/java/com/example/mainserver/dashboard/controller/DashboardController.java#L21)

## DriveLog / 주행로그
- POST /api/drivelogs → 주행 로그 저장
  - Controller: [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L28)
  - Request DTO: [back/main-server/src/main/java/com/example/mainserver/drivelog/dto/DriveLogRequest.java](back/main-server/src/main/java/com/example/mainserver/drivelog/dto/DriveLogRequest.java#L14)
- POST /api/drivelogs/start → 시작 로그 생성
  - Controller: [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L34)
- POST /api/drivelogs/end → 종료 로그 갱신
  - Controller: [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L40)
- GET /api/drivelogs/{id} → ID 조회
  - Controller: [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L54)
- GET /api/drivelogs/car/{carId} → 차량별 조회
  - Controller: [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L60)
- GET /api/drivelogs/range → 기간 조회
  - Controller: [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L68)
- GET /api/drivelogs → 필터 검색
  - Controller: [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L81)
  - Filter DTO: [back/main-server/src/main/java/com/example/mainserver/drivelog/dto/DriveLogFilterRequestDto.java](back/main-server/src/main/java/com/example/mainserver/drivelog/dto/DriveLogFilterRequestDto.java#L19)
- POST /api/drivelogs/update-location → 실시간 위치 업데이트
  - Controller: [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L98)
- GET /api/drivelogs/excel → 엑셀 다운로드
  - Controller: [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L135)

## Sample Request / 샘플 요청
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"loginId":"admin01","password":"pw"}'
```
Source: [back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java](back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java#L26)
