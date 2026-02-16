# Module-Hub-Server / 허브 서버

## Purpose / 목적
RabbitMQ 큐를 소비하여 GPS 로그를 저장하고 차량 위치/누적거리(sumDist)를 갱신합니다.

## Key Components / 주요 컴포넌트
- `ConsumerService`: 큐 소비 및 저장. [back/hub-server/src/main/java/hub/application/ConsumerService.java](back/hub-server/src/main/java/hub/application/ConsumerService.java#L23)
- `LastPositionUpdator`: 배치/지연 업데이트 스케줄링. [back/hub-server/src/main/java/hub/application/LastPositionUpdator.java](back/hub-server/src/main/java/hub/application/LastPositionUpdator.java#L16)
- `CarPostionWriterImpl`: 차량 위치/거리 갱신 및 메인 서버 호출. [back/hub-server/src/main/java/hub/infrastructure/CarPostionWriterImpl.java](back/hub-server/src/main/java/hub/infrastructure/CarPostionWriterImpl.java#L21)
- `GpsLogEntity` + `GpsLogRepository`: GPS 로그 저장. [back/hub-server/src/main/java/hub/domain/GpsLogEntity.java](back/hub-server/src/main/java/hub/domain/GpsLogEntity.java#L15), [back/hub-server/src/main/java/hub/domain/GpsLogRepository.java](back/hub-server/src/main/java/hub/domain/GpsLogRepository.java#L5)
- Error handler: [back/hub-server/src/main/java/hub/exception/handler/GpsConsumerErrorHandler.java](back/hub-server/src/main/java/hub/exception/handler/GpsConsumerErrorHandler.java#L16)

## Config / 설정
- RabbitMQ: [back/hub-server/src/main/java/hub/config/RabbitMqConfig.java](back/hub-server/src/main/java/hub/config/RabbitMqConfig.java#L14)
- Scheduler/Async: [back/hub-server/src/main/java/hub/config/SchedulerConfig.java](back/hub-server/src/main/java/hub/config/SchedulerConfig.java#L10), [back/hub-server/src/main/java/hub/config/AsyncConfig.java](back/hub-server/src/main/java/hub/config/AsyncConfig.java#L12)

## Example / 예시
```java
carPostionWriterImpl.updateOnce(carNumber, lat, lon);
```
Reference: [back/hub-server/src/main/java/hub/infrastructure/CarPostionWriterImpl.java](back/hub-server/src/main/java/hub/infrastructure/CarPostionWriterImpl.java#L21)
