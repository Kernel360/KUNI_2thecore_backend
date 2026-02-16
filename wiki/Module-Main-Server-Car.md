# Module-Main-Server-Car / 메인서버-차량

## APIs / API
- GET /api/cars [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L26)
- GET /api/cars/statistics [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L34)
- GET /api/cars/search [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L48)
- POST /api/cars [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L61)
- PATCH /api/cars [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L73)
- DELETE /api/cars/{car_number} [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L87)
- GET /api/cars/status [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L98)
- GET /api/cars/locations [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L110)

## Key Classes / 주요 클래스
- `CarController`: REST API. [back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java](back/main-server/src/main/java/com/example/mainserver/car/controller/CarController.java#L22)
- `CarService`: 비즈니스 로직. [back/main-server/src/main/java/com/example/mainserver/car/application/CarService.java](back/main-server/src/main/java/com/example/mainserver/car/application/CarService.java#L31)
- `CarWriter` + `CarWriterImpl`: 저장 포트/어댑터. [back/main-server/src/main/java/com/example/mainserver/car/domain/CarWriter.java](back/main-server/src/main/java/com/example/mainserver/car/domain/CarWriter.java#L5), [back/main-server/src/main/java/com/example/mainserver/car/infrastructure/CarWriterImpl.java](back/main-server/src/main/java/com/example/mainserver/car/infrastructure/CarWriterImpl.java#L10)
- `CarReaderImpl` (common): 조회 어댑터. [back/common/src/main/java/com/example/common/infrastructure/car/CarReaderImpl.java](back/common/src/main/java/com/example/common/infrastructure/car/CarReaderImpl.java#L19)
- MyBatis mapper + SQL: [back/main-server/src/main/java/com/example/mainserver/car/infrastructure/mapper/CarMapper.java](back/main-server/src/main/java/com/example/mainserver/car/infrastructure/mapper/CarMapper.java#L11), [back/main-server/src/main/resources/mapper/CarMapper.xml](back/main-server/src/main/resources/mapper/CarMapper.xml#L5)
- DTOs: [back/main-server/src/main/java/com/example/mainserver/car/controller/dto/CarDetailDto.java](back/main-server/src/main/java/com/example/mainserver/car/controller/dto/CarDetailDto.java#L14), [back/main-server/src/main/java/com/example/mainserver/car/controller/dto/CarSearchDto.java](back/main-server/src/main/java/com/example/mainserver/car/controller/dto/CarSearchDto.java#L16)

## Notes / 비고
- Main module contains an empty `CarReaderImpl` stub. Real implementation is in common. [back/main-server/src/main/java/com/example/mainserver/car/infrastructure/CarReaderImpl.java](back/main-server/src/main/java/com/example/mainserver/car/infrastructure/CarReaderImpl.java#L3)
- Search filters are implemented in MyBatis XML. [back/main-server/src/main/resources/mapper/CarMapper.xml](back/main-server/src/main/resources/mapper/CarMapper.xml#L7)
