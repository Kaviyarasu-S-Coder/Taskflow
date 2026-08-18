# Multi-stage Dockerfile for TaskFlow Backend
# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

COPY pom.xml .
COPY src ./src

# Install maven and package application
RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add non-root system user for security
RUN addgroup -S taskflow && adduser -S taskflow -G taskflow
USER taskflow:taskflow

COPY --from=builder /build/target/taskflow-backend-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
