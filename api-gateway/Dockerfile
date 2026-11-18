# ============ Stage 1: build the jar ============
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# 1) Copy only the build files first -> dependencies are cached as a separate layer
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B -q dependency:go-offline

# 2) Copy the source code and build (tests run in CI, not here)
COPY src/ src/
RUN ./mvnw -B -q package -DskipTests

# ============ Stage 2: small runtime image ============
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run as a non-root user
RUN groupadd --system spring && useradd --system --gid spring spring

COPY --from=build /workspace/target/*.jar app.jar

USER spring
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]