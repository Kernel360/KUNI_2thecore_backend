# Module-Main-Server-DriveLog / 메인서버-주행로그

## APIs / API
- POST /api/drivelogs [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L28)
- POST /api/drivelogs/start [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L34)
- POST /api/drivelogs/end [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L40)
- GET /api/drivelogs/{id} [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L54)
- GET /api/drivelogs/car/{carId} [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L60)
- GET /api/drivelogs/range [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L68)
- GET /api/drivelogs (filter) [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L81)
- POST /api/drivelogs/update-location [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L98)
- GET /api/drivelogs/excel [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L135)

## Key Classes / 주요 클래스
- `DriveLogController`: API. [back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java](back/main-server/src/main/java/com/example/mainserver/drivelog/controller/DriveLogController.java#L24)
- `DriveLogService`: 저장/필터/엑셀/실시간 업데이트. [back/main-server/src/main/java/com/example/mainserver/drivelog/application/DriveLogService.java](back/main-server/src/main/java/com/example/mainserver/drivelog/application/DriveLogService.java#L30)
- `DriveLog` entity: [back/main-server/src/main/java/com/example/mainserver/drivelog/domain/DriveLog.java](back/main-server/src/main/java/com/example/mainserver/drivelog/domain/DriveLog.java#L17)
- Reverse geocoding: [back/main-server/src/main/java/com/example/mainserver/drivelog/application/ReverseGeoCodingService.java](back/main-server/src/main/java/com/example/mainserver/drivelog/application/ReverseGeoCodingService.java#L25)
- MyBatis mapper + SQL: [back/main-server/src/main/java/com/example/mainserver/drivelog/infrastructure/mapper/DriveLogMapper.java](back/main-server/src/main/java/com/example/mainserver/drivelog/infrastructure/mapper/DriveLogMapper.java#L11), [back/main-server/src/main/resources/mapper/DriveLogMapper.xml](back/main-server/src/main/resources/mapper/DriveLogMapper.xml#L5)
- Distance utility: [back/main-server/src/main/java/com/example/mainserver/drivelog/util/DistanceCalculator.java](back/main-server/src/main/java/com/example/mainserver/drivelog/util/DistanceCalculator.java#L6)

## Example / 예시
```json
{ "carNumber": "12가1234", "startLatitude": "37.5665", "startLongitude": "126.9780", "startTime": "2025-01-01T12:00:00" }
```
DTO reference: [back/main-server/src/main/java/com/example/mainserver/drivelog/dto/StartDriveRequestDto.java](back/main-server/src/main/java/com/example/mainserver/drivelog/dto/StartDriveRequestDto.java#L13)
