FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY bookstore-app/pom.xml bookstore-app/
COPY bookstore-core/pom.xml bookstore-core/
COPY bookstore-view/pom.xml bookstore-view/
COPY bookstore-database/pom.xml bookstore-database/
COPY bookstore-web/pom.xml bookstore-web/

COPY . .

RUN mvn clean package -DskipTests

FROM tomcat:10.1-jdk17-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=builder /app/bookstore-database/src/main/resources/migration /usr/local/tomcat/migration
COPY --from=builder /app/bookstore-web/target/bookstore.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]