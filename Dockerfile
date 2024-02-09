# Use AdoptOpenJDK 17 as the base image
# Use Eclipse Temurin JDK 17 as the base image
FROM eclipse-temurin:17-jdk as build

# Set the working directory in the Docker image
WORKDIR /workspace/app

# Copy gradle files and the gradle wrapper
COPY gradlew .
COPY gradle gradle
COPY ./app/build.gradle settings.gradle ./

# Copy the source code
COPY ./app/src src

# Build the application
RUN ./gradlew build -x test

# Use Eclipse Temurin JDK 17 for the runtime
FROM eclipse-temurin:17-jdk

# Copy the built artifact from the build stage
COPY --from=build /workspace/app/build/libs/javaobservabilitypoc.jar app.jar

# Expose the application port
EXPOSE 7101

# Run the application
#ENTRYPOINT ["java", "-Dserver.port=7101", "-jar", "/app.jar"]
ENTRYPOINT ["java", "-Dserver.port=7101", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "/app.jar"]
