FROM eclipse-temurin:21-alpine
VOLUME /tmp
EXPOSE 8123
ARG JAR_FILE=target/gate-25.11.04.v1.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]