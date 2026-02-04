# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

COPY src/ src/
RUN ./mvnw -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY --from=build /app/${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
