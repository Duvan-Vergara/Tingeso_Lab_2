FROM openjdk:17
ARG JAR_FILE=target/backend-gateway.jar
COPY ${JAR_FILE} backend-gateway.jar
ENTRYPOINT ["java","-jar","/backend-gateway.jar"]