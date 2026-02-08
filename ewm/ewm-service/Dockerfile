# Stage 1: build
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# Копируем весь проект в контейнер
COPY . .

# Собираем весь multi-module проект
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

# Копируем только собранный jar ewm-service
COPY --from=build /app/ewm/ewm-service/target/ewm-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
