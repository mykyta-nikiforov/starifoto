# 🌐 PhotoMap Supercluster API

This guide provides instructions for building and deploying the PhotoMap Supercluster API.

## 📋 Prerequisites

- Docker and Docker Compose installed on your machine
- Access to a DigitalOcean droplet or local environment

## 🚀 Building and Running

### Environment Configuration

1. **Environment File**: Create a `docker/.env` file with the following required variables:

```env
# MongoDB Configuration
MONGO_CONNECTION=mongodb://mongo1:27017/photomap?directConnection=true  # MongoDB connection string
MONGO_AUTH_SOURCE=photomap                                             # MongoDB authentication database
MONGO_USERNAME=photomapUser                                           # MongoDB username
MONGO_PASSWORD=password                                               # MongoDB password

# CORS Configuration
CORS_ORIGIN=http://localhost:5173                                     # Frontend URL for CORS (in production: https://starifoto.in.ua)
```

### Build Docker Image

1. **Build Locally**:
   ```bash
   docker build -t supercluster-api .
   ```
   This command builds the Docker image for the Supercluster API.

2. **Build for Multiple Architectures**:
   ```bash
   docker buildx build --platform linux/amd64,linux/arm64 \
     -t ${REGISTRY_URL}/${PROJECT_ID}/${REPOSITORY_NAME}/photomap-supercluster:${TAG} \
     --push .
   ```
   This command builds and pushes the image for multiple architectures.

   Required environment variables:
   - `REGISTRY_URL`: Container registry URL (e.g., europe-west1-docker.pkg.dev)
   - `PROJECT_ID`: GCP project ID
   - `REPOSITORY_NAME`: Artifact Registry repository name
   - `TAG`: Image tag (e.g., latest)

### Run with Docker Compose

1. **Start Services**:
   ```bash
   cd docker
   docker compose -p starifoto up -d
   ```
   This command starts all services defined in the Docker Compose file.

## 🔧 Useful Commands

- **Stop All Services**:
  ```bash
  docker compose -p starifoto down
  ```
  This command stops all running services and removes the containers.

- **Check Logs**:
  Use the following command to view logs for a specific service:
  ```bash
  docker compose logs <service-name>
  ```

## 🗂️ Directory Structure

- `docker/`: Contains Docker Compose configuration and related files
  - `.env`: Environment variables configuration
  - `docker-compose.yaml`: Service definitions

---
Made with ❤️ for Ukraine 🇺🇦