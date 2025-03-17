#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Google Cloud configurations
DOCKER_TAG="latest"

echo "Building photomap-supercluster..."
npm install
echo "Dockerizing photomap-supercluster..."
docker build -t "photomap-supercluster:$DOCKER_TAG" .

echo "photomap-supercluster has been built and Dockerized!"
