FROM openjdk:17
ARG JAR_FILE=target/backend-config.jar
COPY ${JAR_FILE} backend-config.jar
ENTRYPOINT ["java","-jar","/backend-config.jar"]