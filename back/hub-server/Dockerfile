FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ARG JAR_NAME=app.hub.jar

COPY ${JAR_NAME} app.jar

ENV JAVA_TOOL_OPTIONS="-Xms256m -Xmx1024m -Dfile.encoding=UTF-8"
ENTRYPOINT ["java", "-jar", "app.jar"]





# 전버전
# Dockerfile.hub
#FROM eclipse-temurin:17-jre-alpine
#WORKDIR /app

# Jenkins에서 미리 빌드된 JAR 복사
#COPY release/hub-server/app.jar app.jar

#ENV JAVA_TOOL_OPTIONS="-Xms256m -Xmx1024m -Dfile.encoding=UTF-8"
#EXPOSE 8082
#ENTRYPOINT ["java","-jar","/app/app.jar"]