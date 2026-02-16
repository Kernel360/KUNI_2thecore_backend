# Deployment / 배포

## Docker (Local/EC2) / Docker 배포
- Compose file: [back/docker/docker-compose.yml](back/docker/docker-compose.yml#L1)
- Main Dockerfile: [back/main-server/Dockerfile](back/main-server/Dockerfile#L1)
- Hub Dockerfile: [back/hub-server/Dockerfile](back/hub-server/Dockerfile#L1)

### Steps / 절차
```bash
cd back/docker
docker compose --env-file ../prod.env up -d --build
```

Environment variables: [back/prod.env](back/prod.env#L1)

## CI/CD (Jenkins) / Jenkins 파이프라인
Pipeline builds JARs, packages Docker bundle, and deploys to EC2.
파이프라인은 JAR 빌드 후 번들링하고 EC2에 배포합니다.

Reference: [back/Jenkinsfile](back/Jenkinsfile#L1)

## Health/Monitoring / 헬스체크
- Actuator exposure in main: [back/main-server/src/main/resources/application.yml](back/main-server/src/main/resources/application.yml#L4)
- Actuator exposure in hub: [back/hub-server/src/main/resources/application.yml](back/hub-server/src/main/resources/application.yml#L42)
