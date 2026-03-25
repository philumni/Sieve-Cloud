# ── Stage 1: Build the WAR ────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first (layer-cached when pom.xml is unchanged)
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -q -DskipTests

# ── Stage 2: Run on Tomcat ────────────────────────────────────────────────────
FROM tomcat:10.1-jre17

# Remove the default Tomcat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy our WAR as ROOT so it is served at /
COPY --from=build /app/target/sieve-app.war /usr/local/tomcat/webapps/ROOT.war

# Cloud Run injects $PORT; Tomcat defaults to 8080 which matches Cloud Run's default.
# Override only if needed via -e PORT=xxxx.
EXPOSE 8080

CMD ["catalina.sh", "run"]
