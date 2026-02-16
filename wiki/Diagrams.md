# Diagrams / 다이어그램

## Architecture / 아키텍처
```mermaid
flowchart LR
  Client --> Main[Main Server]
  Client --> Hub[Hub Server]
  Main --> MQ[(RabbitMQ)]
  MQ --> Hub
  Main --> Redis[(Redis)]
  Main --> DB[(MySQL)]
  Hub --> DB
```

Code references: [back/main-server/src/main/java/com/example/mainserver/collector/infrastructure/rabbitmq/GpsLogProducer.java](back/main-server/src/main/java/com/example/mainserver/collector/infrastructure/rabbitmq/GpsLogProducer.java#L11), [back/hub-server/src/main/java/hub/application/ConsumerService.java](back/hub-server/src/main/java/hub/application/ConsumerService.java#L23)

## Sequence / 시퀀스
```mermaid
sequenceDiagram
  participant Collector
  participant MQ
  participant Hub
  participant Main
  participant DB

  Collector->>Main: POST /api/logs/gps
  Main->>MQ: publish gps.data.*
  MQ-->>Hub: gps.data.queue
  Hub->>DB: save gps_log
  Hub->>Main: POST /api/drivelogs/update-location
  Main->>DB: update drive_log
```

## Class Diagram / 클래스 다이어그램
```mermaid
classDiagram
  class CarEntity {
    +int id
    +String carNumber
    +String brand
    +String model
    +double sumDist
  }
  class DriveLog {
    +Long driveLogId
    +Long carId
    +LocalDateTime startTime
    +LocalDateTime endTime
    +BigDecimal driveDist
  }
  class GpsLogEntity {
    +int id
    +String carNumber
    +String latitude
    +String longitude
  }

  DriveLog --> CarEntity : carId
  GpsLogEntity --> CarEntity : carNumber
```

References: [back/common/src/main/java/com/example/common/domain/car/CarEntity.java](back/common/src/main/java/com/example/common/domain/car/CarEntity.java#L16), [back/main-server/src/main/java/com/example/mainserver/drivelog/domain/DriveLog.java](back/main-server/src/main/java/com/example/mainserver/drivelog/domain/DriveLog.java#L17), [back/hub-server/src/main/java/hub/domain/GpsLogEntity.java](back/hub-server/src/main/java/hub/domain/GpsLogEntity.java#L15)
