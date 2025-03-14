FROM maven:3-openjdk-17 AS build

COPY pom.xml /usr/app/pom.xml
RUN mvn dependency:go-offline -f /usr/app/pom.xml

COPY src /usr/app/src
RUN mvn -f /usr/app/pom.xml clean package

FROM alpine:3

RUN apk add --no-cache openjdk17-jre

COPY --from=build /usr/app/target/signature-service-*.jar /usr/app/signature-service.jar

CMD ["java", "-jar", "/usr/app/signature-service.jar"]