FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY mvnw .
COPY .mvn ./.mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests -Powasp-skip

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/locus-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

RUN groupadd --system app && useradd --system -g app app
USER app

ENTRYPOINT ["java", "-jar", "/app/app.jar"]