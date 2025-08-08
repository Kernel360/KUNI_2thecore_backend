# ===== Build stage =====
FROM gradle:8.4.0-jdk17 AS build
WORKDIR /workspace

# 캐시 최적화: 설정/래퍼/서브모듈 gradle 파일만 먼저 복사
COPY settings.gradle build.gradle gradlew gradle/ ./
COPY common/build.gradle common/build.gradle
COPY hub-server/build.gradle hub-server/build.gradle
COPY main-server/build.gradle main-server/build.gradle

# 의존성만 먼저 받아 캐시 고정
RUN chmod +x ./gradlew && ./gradlew --no-daemon :main-server:dependencies || true

# 실제 소스 반영
COPY . .

# 메인서버 빌드 (테스트 제외)
RUN ./gradlew --no-daemon :main-server:bootJar -x test


# ===== Runtime stage =====
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드 산출물 복사
COPY --from=build /workspace/main-server/build/libs/*.jar app.jar

# 내부 포트 8080
EXPOSE 8080

# 비루트 유저 (선택)
RUN useradd -r -u 1001 appuser
USER appuser

# 실행 (경로 수정: /app/app.jar)
ENTRYPOINT ["java", "-jar", "app.jar"]