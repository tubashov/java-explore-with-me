FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/stats/stats-server/target/stats-server-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
