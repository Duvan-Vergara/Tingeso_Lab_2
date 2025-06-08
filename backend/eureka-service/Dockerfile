FROM openjdk:17
ARG JAR_FILE=target/backend-eureka.jar
COPY ${JAR_FILE} backend-eureka.jar
ENTRYPOINT ["java","-jar","/backend-eureka.jar"]