FROM gradle:8.4.0-jdk17 AS build

WORKDIR /app

# 전체 프로젝트 복사
COPY ../../ .

# hub-server만 빌드
RUN gradle :hub-server:build -x test

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/hub-server/build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]