FROM gradle:8.4.0-jdk17 AS build
WORKDIR /workspace

# 캐시 최적화: Gradle 메타 먼저
COPY settings.gradle build.gradle gradlew gradle/ ./
COPY common/build.gradle common/build.gradle
COPY hub-server/build.gradle hub-server/build.gradle
COPY main-server/build.gradle main-server/build.gradle

RUN chmod +x ./gradlew && ./gradlew --no-daemon :main-server:dependencies || true

# 소스 복사 후 빌드
COPY . .
RUN ./gradlew --no-daemon :main-server:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/main-server/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]