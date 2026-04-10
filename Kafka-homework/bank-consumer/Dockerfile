FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
COPY bank-common/pom.xml bank-common/
COPY bank-producer/pom.xml bank-producer/
COPY bank-consumer/pom.xml bank-consumer/

RUN mvn dependency:go-offline -B
COPY bank-common/src bank-common/src
COPY bank-consumer/src bank-consumer/src

RUN mvn clean package -pl bank-consumer -am -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /build/bank-consumer/target/bank-consumer.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]