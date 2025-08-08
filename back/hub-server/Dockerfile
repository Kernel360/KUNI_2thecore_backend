# ==== Build stage ====
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# 캐시 효율을 위해 설정/의존성 메타 먼저 복사
COPY settings.gradle build.gradle ./
COPY gradle gradle
COPY gradlew ./
# 서브모듈의 build.gradle만 선반영
COPY common/build.gradle common/build.gradle
COPY hub-server/build.gradle hub-server/build.gradle
COPY main-server/build.gradle main-server/build.gradle

# 의존성만 먼저 받아 캐시 고정
RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies || true

# 실제 소스 복사
COPY . .

# 허브 서버만 빌드
RUN ./gradlew --no-daemon :hub-server:bootJar -x test

# ==== Runtime stage ====
FROM eclipse-temurin:17-jre
WORKDIR /app

# JAR 복사
COPY --from=build /app/hub-server/build/libs/*.jar app.jar

# 허브 내부 포트 8082
EXPOSE 8082

# 비루트 유저(선택)
RUN useradd -r -u 1001 appuser
USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]