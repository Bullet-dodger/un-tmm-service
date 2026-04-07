# ═══════════════════════════════════════════════════════════════
#  Stage 1 — Build the Spring Boot JAR
# ═══════════════════════════════════════════════════════════════
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy Gradle wrapper first so dependency downloads are cached
# in a separate layer (only invalidated when build.gradle changes)
COPY gradlew .
COPY gradle/ gradle/
RUN chmod +x gradlew

COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon -q 2>/dev/null || true

# Full source build
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# ═══════════════════════════════════════════════════════════════
#  Stage 2 — Minimal runtime image
# ═══════════════════════════════════════════════════════════════
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
