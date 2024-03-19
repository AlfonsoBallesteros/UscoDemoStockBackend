FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /workspace
COPY ./ .
RUN mvn clean package spring-boot:repackage
CMD ["mvn", "-version"]

FROM eclipse-temurin:17-jre-alpine AS deploy
WORKDIR /workspace
COPY --from=builder /workspace/target/*.jar ./app.jar
EXPOSE 8888
CMD ["java", "-jar", "/workspace/app.jar"]
