FROM openjdk:17
WORKDIR /usr/app
COPY target/AuthenticationService-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
