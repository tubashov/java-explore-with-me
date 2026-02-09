# Stage 1: build
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# Копируем pom.xml для кеша
COPY pom.xml ./
COPY ewm ./ewm
COPY stats ./stats

# Загружаем зависимости
RUN mvn dependency:go-offline -B

# Копируем весь исходный код
COPY . .

# Сборка приложения
RUN mvn clean package -DskipTests -B

# Структура
RUN ls -R /app

# Stage 2: runtime
FROM amazoncorretto:21.0.6

WORKDIR /app

# Копируем готовый jar
COPY --from=build /app/stats/stats-server/target/stats-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
