# 🐳 Docker Setup for PhotoMap Core API

This guide provides instructions for setting up and running the PhotoMap Core API using Docker Compose.

## 📋 Prerequisites

- Docker and Docker Compose installed on your machine
- Access to a DigitalOcean droplet or local environment
- Google Cloud service account credentials file

## 🚀 Getting Started

### Environment Configuration

1. **Google Cloud Setup**:
   Create a `google` directory in the parent folder and place your Google Cloud service account credentials file there:
   ```
   photomap-core-api/
   ├── google/
   │   └── credentials.json    # Google Cloud service account credentials
   └── docker/
       └── docker-compose.yaml
   ```
   This file is required for the photo-api service to interact with Google Cloud services.

2. **Environment File**: Create a `.env` file with the following required variables:

```env
# Database Configuration
POSTGRES_USER=admin                     # PostgreSQL admin username
POSTGRES_PASSWORD=Password123           # PostgreSQL admin password
POSTGRES_DB_NAME=photomap              # PostgreSQL database name
DATABASE_URL=jdbc:postgresql://postgresdb:5432/${POSTGRES_DB_NAME}

# MongoDB Configuration
MONGO_INITDB_ROOT_USERNAME=root        # MongoDB root username
MONGO_INITDB_ROOT_PASSWORD=rootPassword # MongoDB root password
MONGO_PHOTOMAP_DB_NAME=photomap        # MongoDB database name
MONGO_PHOTOMAP_USERNAME=photomapUser   # MongoDB application user
MONGO_PHOTOMAP_PASSWORD=password       # MongoDB application password

# API Configuration
PHOTO_API_SPRING_PROFILES_ACTIVE=local # Spring profile (local/dev/prod)
PHOTOMAP_CLIENT_URL=http://localhost:5173 # Frontend URL for CORS

# Email Configuration
NOTIFICATION_API_NO_REPLY_EMAIL=no-reply@example.com        # Notification sender email
NOTIFICATION_API_NO_REPLY_EMAIL_PASSWORD=your-email-password # Email password

# Kafka Configuration
KAFKA_DOCKER_EXTERNAL_HOSTNAME=localhost # Kafka hostname

# Security Configuration
JWT_SECRET=your-jwt-secret              # Secret for JWT token generation
SWAGGER_PASSWORD=your-swagger-password   # Password for Swagger UI access

# Google Authentication
GOOGLE_OAUTH_CLIENT_ID=your-client-id   # Google OAuth 2.0 client ID
```

### Docker Compose Commands

1. **Start All Services**:
   ```bash
   docker-compose -p photomap up -d
   ```
   This command will start all services defined in the `docker-compose.yaml` file.

2. **Start a Specific Service**:
   ```bash
   docker-compose -p photomap up -d <service-name>
   ```
   Replace `<service-name>` with the name of the service you want to start (e.g., `notification-api`).

3. **Stop All Services**:
   ```bash
   docker-compose -p photomap down
   ```
   This command will stop all running services and remove the containers.

### 🛠️ Useful Scripts

- **Local Dockerization**:
  Run the `dockerize-local.sh` script to build Docker images for all services locally.
  ```bash
  ./dockerize-local.sh
  ```

## 🗂️ Directory Structure

- `docker-compose.yaml`: Main Docker Compose configuration file
- `.env`: Environment variable file
- `dockerize-local.sh`: Script for local Docker setup
- `postgres-db-init-script/`: Initialization scripts for PostgreSQL
- `mongo/`: MongoDB configuration and setup files

## 🔧 Troubleshooting

- **Check Logs**:
  Use the following command to view logs for a specific service:
  ```bash
  docker-compose logs <service-name>
  ```

---
Made with ❤️ for Ukraine 🇺🇦