# Module-Main-Server-Collector / 메인서버-수집

## APIs / API
- POST /api/logs/gps [back/main-server/src/main/java/com/example/mainserver/collector/controller/CollectorController.java](back/main-server/src/main/java/com/example/mainserver/collector/controller/CollectorController.java#L21)
- POST /api/logs/gps-direct [back/main-server/src/main/java/com/example/mainserver/collector/controller/CollectorController.java](back/main-server/src/main/java/com/example/mainserver/collector/controller/CollectorController.java#L30)

## Key Classes / 주요 클래스
- `CollectorController`: 수집 API. [back/main-server/src/main/java/com/example/mainserver/collector/controller/CollectorController.java](back/main-server/src/main/java/com/example/mainserver/collector/controller/CollectorController.java#L16)
- `CollectorService`: 검증 및 발행. [back/main-server/src/main/java/com/example/mainserver/collector/application/CollectorService.java](back/main-server/src/main/java/com/example/mainserver/collector/application/CollectorService.java#L23)
- `GpsLogProducer`: RabbitMQ 발행자. [back/main-server/src/main/java/com/example/mainserver/collector/infrastructure/rabbitmq/GpsLogProducer.java](back/main-server/src/main/java/com/example/mainserver/collector/infrastructure/rabbitmq/GpsLogProducer.java#L11)
- `RabbitMqConfig`: exchange/queue 설정. [back/main-server/src/main/java/com/example/mainserver/collector/config/RabbitMqConfig.java](back/main-server/src/main/java/com/example/mainserver/collector/config/RabbitMqConfig.java#L14)
- `GpsLogEntity` + DTOs: [back/main-server/src/main/java/com/example/mainserver/collector/domain/GpsLogEntity.java](back/main-server/src/main/java/com/example/mainserver/collector/domain/GpsLogEntity.java#L16), [back/main-server/src/main/java/com/example/mainserver/collector/domain/dto/GpsLogDto.java](back/main-server/src/main/java/com/example/mainserver/collector/domain/dto/GpsLogDto.java#L20)
- `GpxExceptionHandler`: 스파이크/프리즈 감지. [back/main-server/src/main/java/com/example/mainserver/collector/application/GpxExceptionHandler.java](back/main-server/src/main/java/com/example/mainserver/collector/application/GpxExceptionHandler.java#L14)

## Example / 예시
```json
{ "carNumber": "12가1234", "logList": [{"latitude":"37.5665","longitude":"126.9780","timestamp":"2025-01-01T12:00:00"}] }
```
DTO reference: [back/main-server/src/main/java/com/example/mainserver/collector/domain/dto/GpsLogDto.java](back/main-server/src/main/java/com/example/mainserver/collector/domain/dto/GpsLogDto.java#L20)
