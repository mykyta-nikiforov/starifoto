#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Build and publish shared library
echo "Building and publishing photomap-common..."
cd ../photomap-common
./gradlew build publishToMavenLocal
cd ..

# List of microservices
microservices=("api-gateway" "geojson-generator" "photo-api" "user-api" "notification-api")

# Loop through each microservice to build and Dockerize it
for ms in "${microservices[@]}"
do
    echo "Building $ms..."
    cd "$ms"
    ./gradlew build bootJar

    echo "Dockerizing $ms..."
    docker build -t "$ms":latest .

    cd ..
done

echo "All microservices have been built and Dockerized!"
