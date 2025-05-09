FROM openjdk:17-alpine

ARG JAR_FILE_PATH=build/libs/hhplus-e-commerce-*.jar
COPY ${JAR_FILE_PATH} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]