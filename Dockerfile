FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar /app/technostore.jar

ENTRYPOINT ["java", "-jar", "/app/technostore.jar"]