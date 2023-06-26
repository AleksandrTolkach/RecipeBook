FROM maven:3.9.0-eclipse-temurin-17-focal

WORKDIR /app

COPY mvnw pom.xml ./
COPY . /app

CMD ./mvnw package && ./mvnw site && ./mvnw spring-boot:run