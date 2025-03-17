#!/bin/bash
set -e

# Assuming each microservice has a Dockerfile in its directory
microservices=("api-gateway" "geojson-generator" "photo-api" "user-api" "notification-api")

# Loop through each microservice to Dockerize it
for ms in "${microservices[@]}"
do
    echo "Dockerizing $ms..."
    cd "$ms"
    docker build -t ${_REGISTRY_URL}/${PROJECT_ID}/${_REPOSITORY_NAME}/$ms:latest \
                 -t ${_REGISTRY_URL}/${PROJECT_ID}/${_REPOSITORY_NAME}/$ms:${SHORT_SHA} .
    cd ..
done

echo "All microservices have been Dockerized!"
