# Dockerfile.hub
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Jenkins에서 미리 빌드된 JAR 복사
COPY release/hub-server/app.jar app.jar

ENV JAVA_TOOL_OPTIONS="-Xms256m -Xmx1024m -Dfile.encoding=UTF-8"
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]