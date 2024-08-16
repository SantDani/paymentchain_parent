# Use OpenJDK 17 with Alpine Linux as the base image
FROM openjdk:17-jdk-alpine

# Create a new group 'paymentchain' and a new user 'admin', then add 'admin' to the 'paymentchain' group
RUN addgroup -S paymentchain && adduser -S admin -G paymentchain

# Install curl for network connectivity checks and other utilities
RUN apk add --no-cache curl

# Switch to the 'admin' user for running the application
USER admin:paymentchain

# Define a volume for temporary files
VOLUME /tmp

# Define a build argument for the JAR file location
ARG JAR_FILE=target/*.jar

# Copy the JAR file to the root directory of the container as 'app.jar'
ADD target/${JAR_FILE} app.jar

# Set an environment variable for Java options (empty by default)
ENV JAVA_OPTS=""

# The entry point to run the application using 'sh' with the provided Java options
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
