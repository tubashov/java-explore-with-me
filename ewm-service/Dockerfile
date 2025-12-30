# Используем образ с JDK 17
FROM eclipse-temurin:17-jdk-alpine

# Создаем рабочую директорию
WORKDIR /app

# Копируем pom.xml и код
COPY pom.xml .
COPY src ./src

# Сборка jar
RUN ./mvnw clean package -DskipTests

# Указываем порт
EXPOSE 8080

# Команда запуска
CMD ["java", "-jar", "target/ewm-service-0.0.1-SNAPSHOT.jar"]
