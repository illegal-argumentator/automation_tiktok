FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml ./
RUN mvn dependency:resolve

COPY src ./src
RUN mvn clean package -DskipTests

RUN ls -lh target

FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /build/target/automation-tiktok*.jar automation-tiktok.jar

EXPOSE 4094

CMD ["java", "-jar", "automation-tiktok.jar"]
