# 🌐 PhotoMap UI

This guide provides instructions for building and deploying the PhotoMap UI using Docker.

## 📋 Prerequisites

- Docker and Docker Compose installed on your machine
- Access to a DigitalOcean droplet or local environment

## 🚀 Building and Running

### Environment Configuration

1. **Environment File**: Create a `docker/.env` file with the following required variables:

```env
# API Configuration
NUXT_PUBLIC_BASE_API_URL=http://api-gateway:8081        # Internal API URL for container communication
NUXT_PUBLIC_BROWSER_BASE_API_URL=http://localhost:8081  # API URL for browser requests
NUXT_PUBLIC_CLUSTER_API_URL=http://localhost:3000       # Supercluster API URL
NUXT_PUBLIC_TIMEOUT=60000                               # API request timeout in milliseconds

# WebSocket Configuration
NUXT_PUBLIC_WS_URL=ws://localhost:8084/ws               # WebSocket server URL

# Google Integration
NUXT_PUBLIC_GOOGLE_CLIENT_ID=your-google-client-id      # Google OAuth client ID
NUXT_PUBLIC_GOOGLE_MAPS_API_KEY=your-maps-api-key       # Google Maps API key

# Application Configuration
NUXT_PUBLIC_NODE_ENV=local                              # Environment (local/dev/prod)
NUXT_PUBLIC_CLIENT_URL=http://localhost:5173            # Frontend URL
```

### Build Docker Image

1. **Build Locally**:
   ```bash
   docker build -t photomap-nuxt-ui .
   ```
   This command builds the Docker image for the PhotoMap UI.

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