FROM amazoncorretto:21-al2023-jdk
WORKDIR /app

# Copy the target directory containing the JAR file
COPY target ./target

# Copy the JAR file to the app.jar
COPY target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]