FROM maven:3.9.6-eclipse-temurin-21 as builder
WORKDIR /build

COPY . .
RUN mvn -pl payment -T 1C install

FROM eclipse-temurin:21-jre
WORKDIR /build

COPY --from=builder /build/payment/target/payment-*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
