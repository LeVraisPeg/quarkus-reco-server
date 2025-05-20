FROM eclipse-temurin:21-jdk AS build

COPY --chown=185 mvnw .
COPY --chown=185 .mvn .mvn
COPY --chown=185 pom.xml .
COPY --chown=185 src src

RUN ./mvnw clean package -DskipTests

FROM registry.access.redhat.com/ubi9/openjdk-21:1.21

ENV LANGUAGE='en_US:en'

COPY --from=build --chown=185 target/*.jar /deployments/app.jar

EXPOSE 8080
USER 185
ENV JAVA_APP_JAR="/deployments/app.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]