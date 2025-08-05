FROM gradle:8.4.0-jdk17 AS build

WORKDIR /app

# 전체 프로젝트 복사
COPY ../../ .

# main-server만 빌드
RUN gradle :main-server:build -x test

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=build /app/main-server/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]