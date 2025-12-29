#FROM eclipse-temurin:21-jre
#
#WORKDIR /app
#
#COPY target/stats-server-0.0.1-SNAPSHOT.jar app.jar
#
#EXPOSE 9090
#
#ENTRYPOINT ["java","-jar","/app/app.jar"]
FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=target/stats-server.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
