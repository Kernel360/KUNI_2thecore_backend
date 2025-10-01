FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ARG JAR_NAME=app.main.jar

COPY ${JAR_NAME} app.jar

ENV JAVA_TOOL_OPTIONS="-Xms256m -Xmx1024m -Dfile.encoding=UTF-8"
ENTRYPOINT ["java", "-jar", "app.jar"]




# 전 버전
# Dockerfile.main
# FROM eclipse-temurin:17-jre-alpine
# WORKDIR /app

# Jenkins에서 미리 빌드된 JAR 복사
# COPY release/main-server/app.jar app.jar

# ENV JAVA_TOOL_OPTIONS="-Xms256m -Xmx1024m -Dfile.encoding=UTF-8"
# EXPOSE 8080
# ENTRYPOINT ["java","-jar","/app/app.jar"]