#!/bin/bash
# Cross-platform build script for Unix-like systems (macOS, Linux)

echo "Building Dispositivo IoT Project..."
echo "Java version check:"
java -version

echo ""
echo "Building core modules..."
mvn clean compile

echo ""
echo "Running tests..."
mvn test

echo ""
echo "Creating executable JARs..."
mvn package

echo ""
echo "Build completed! Executable JARs are available in:"
echo "- dispositivo-core/target/dispositivo-core-1.0.0.jar"
echo "- dispositivo-pi4j/target/dispositivo-pi4j-1.0.0.jar"

echo ""
echo "To run the applications:"
echo "Core device: java -jar dispositivo-core/target/dispositivo-core-1.0.0.jar"
echo "Pi4J device: java -jar dispositivo-pi4j/target/dispositivo-pi4j-1.0.0.jar"