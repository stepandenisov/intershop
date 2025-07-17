FROM alpine/java:21-jdk
ADD . /src
WORKDIR /src
RUN ./mvnw package
EXPOSE 8080
ENTRYPOINT ["java","-jar","target/intershop-0.0.1-SNAPSHOT.jar"]