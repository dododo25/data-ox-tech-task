#
# Build stage
#
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /task
COPY . /task
RUN chmod +x ./mvnw && ./mvnw -f ./pom.xml clean package
RUN rm -rf ./task/admin ./task/core ./task/migrations

#
# Migrations package stage
#
FROM openjdk:17-jdk-slim
WORKDIR /task/client
COPY --from=build ./task/client/target/client-0.0.1.jar .
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "./client-0.0.1.jar"]
