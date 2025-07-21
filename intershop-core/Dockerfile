FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /build

WORKDIR /app

COPY ../ /app/

RUN mvn clean install -pl payment-client -am

RUN mvn clean install -pl intershop-core -am

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/intershop-core/target/intershop-core-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
