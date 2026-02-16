# Getting Started / 시작 가이드

## Prerequisites / 준비사항
- Java 17
- Docker (optional)
- MySQL, Redis, RabbitMQ (or Docker Compose)

## Build / 빌드
```bash
cd back
./gradlew build
```

References: [back/build.gradle](back/build.gradle#L1), [back/settings.gradle](back/settings.gradle#L1)

## Run (Main) / 메인서버 실행
```bash
cd back
./gradlew :main-server:bootRun
```

Config: [back/main-server/src/main/resources/application.yml](back/main-server/src/main/resources/application.yml#L1)

## Run (Hub) / 허브서버 실행
```bash
cd back
./gradlew :hub-server:bootRun
```

Config: [back/hub-server/src/main/resources/application.yml](back/hub-server/src/main/resources/application.yml#L1)

## Docker Compose / 도커 컴포즈
```bash
cd back/docker
docker compose --env-file ../prod.env up -d --build
```

Compose: [back/docker/docker-compose.yml](back/docker/docker-compose.yml#L1)
