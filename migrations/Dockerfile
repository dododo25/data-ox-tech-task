#
# Build stage
#
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /task
COPY . /task
RUN chmod +x ./mvnw && ./mvnw -f ./pom.xml clean package
RUN rm -rf ./task/admin ./task/client ./task/core

#
# Migrations package stage
#
FROM openjdk:17-jdk-slim
WORKDIR /task/migrations
COPY --from=build ./task/migrations/target/migrations-0.0.1.jar .
EXPOSE 8081
ENTRYPOINT ["java", "-Duser.timezone=\"UTC\"", "-jar", "./migrations-0.0.1.jar"]
