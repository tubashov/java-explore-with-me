FROM maven:latest AS build

WORKDIR /app

# копируем весь проект
COPY . .

# собираем только ewm-service + зависимости
RUN mvn clean package -pl ewm/ewm-service -am -DskipTests

FROM amazoncorretto:17

WORKDIR /app

COPY --from=build /app/ewm/ewm-service/target/ewm-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
