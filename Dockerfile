FROM openjdk:21-slim-bookworm

WORKDIR /app

COPY target/*.jar /app/technostore.jar

ENTRYPOINT ["java", "-jar", "/app/technostore.jar"]