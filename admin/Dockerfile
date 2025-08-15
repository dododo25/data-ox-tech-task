#
# Build stage
#
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /task
COPY .. /task/
RUN chmod +x ./mvnw && ./mvnw -f ./pom.xml clean package
RUN rm -rf ./task/client ./task/core ./task/migrations

#
# Admin package stage
#
FROM openjdk:17-jdk-slim
WORKDIR /task/admin
COPY --from=build ./task/admin/target/admin-0.0.1.jar .
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "./admin-0.0.1.jar"]