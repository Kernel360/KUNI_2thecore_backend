# hub-server/Dockerfile
FROM gradle:8.4.0-jdk17 AS build
WORKDIR /workspace
COPY settings.gradle build.gradle gradlew gradle/ ./
COPY common/build.gradle common/build.gradle
COPY hub-server/build.gradle hub-server/build.gradle
RUN chmod +x ./gradlew && ./gradlew --no-daemon :hub-server:dependencies || true

COPY . .
RUN ./gradlew --no-daemon :hub-server:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/hub-server/build/libs/*.jar app.jar
EXPOSE 8081   # ← 허브 실제 리스닝 포트
ENTRYPOINT ["java","-jar","app.jar"]