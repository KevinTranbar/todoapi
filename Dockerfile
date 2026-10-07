# Stage 1
FROM maven:3.9-eclipse-temurin-26 AS build
# Maven, Java 26, Verktyg för att kompilera projekt
WORKDIR /app
# Arbetsmapp i container = /app
COPY pom.xml .
RUN mvn dependency:go-offline
# Kopiera in pom.xml i image / Ladda ner dependencies
COPY src ./src
RUN mvn clean package -DskipTests
# Kopiera sourcecode och bygg app

# Stage 2
FROM eclipse-temurin:26-jre
# Linux, Java 26 (Skippar Maven eftersom projekt redan byggt i Stage 1)
WORKDIR /app
# Samma som förra
COPY --from=build /app/target/*.jar app.jar
# Kopiera .jar fil från Stage 1
EXPOSE 8080
# Vilken port applikationen / containern lyssnar på
ENTRYPOINT ["java", "-jar", "app.jar"]
# Kommando för att starta applikation i container