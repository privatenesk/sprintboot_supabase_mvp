# Use a base image with Java 21 and Maven
FROM maven:3.9.6-eclipse-temurin-21

# Set the working directory
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Run the tests
CMD ["mvn", "test"]
