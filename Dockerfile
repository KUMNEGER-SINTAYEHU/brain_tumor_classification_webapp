# Use a lightweight Java image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from your local machine to the container
COPY target/braintumorclassifier-0.0.1-SNAPSHOT.jar app.jar  # This line uses your JAR file name

# Expose port 8080 for the Spring Boot app
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
