FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
# Копируем заглушку JAR, если его нет — создаем пустой JAR
ARG JAR_FILE=target/empty.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]