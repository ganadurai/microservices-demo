FROM openjdk:11 AS openjdk

WORKDIR /app

RUN mkdir /app/dependency

COPY target/dependency/*.* /app/dependency/
COPY target/onlineboutique-api-client-1.0 /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/onlineboutique-api-client-1.0.jar"]
