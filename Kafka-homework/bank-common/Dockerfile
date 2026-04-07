FROM maven:3.8.4-openjdk-17-slim AS builder

WORKDIR /build

COPY pom.xml .
COPY bank-common/pom.xml bank-common/
COPY bank-producer/pom.xml bank-producer/
COPY bank-consumer/pom.xml bank-consumer/

RUN mvn dependency:go-offline -B
COPY bank-common/src bank-common/src
COPY bank-producer/src bank-producer/src
RUN mvn clean package -pl bank-producer -am -DskipTests
FROM openjdk:17-slim
WORKDIR /app

COPY --from=builder /build/bank-producer/target/bank-producer.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]