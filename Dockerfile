FROM eclipse-temurin:26-jdk AS build
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw
COPY src src
RUN ./mvnw -DskipTests package

FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /app/target/Diary-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
