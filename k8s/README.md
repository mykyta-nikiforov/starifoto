# Kubernetes Configuration for СтаріФото.Укр

This directory contains Kubernetes manifests for deploying the СтаріФото.Укр application in a Kubernetes environment. This configuration was previously used when the application was deployed on Google Kubernetes Engine (GKE).

> **Note:** The application has since been migrated to run on a Digital Ocean droplet using Docker containers.

## Directory Structure

```
k8s/
├── core-api/                 # Backend core services
│   ├── account/             # Service accounts and RBAC
│   ├── config/              # Configuration maps
│   ├── kafka/              # Kafka and Zookeeper
│   ├── microservices/      # Java microservices configs
│   ├── mongo/              # MongoDB configurations
│   ├── redis/              # Redis cache and pub/sub
│   └── secrets/            # Backend secrets
├── supercluster-api/        # Map clustering service
├── ui/                      # Frontend application
│   ├── secrets/            # Frontend secrets
│   ├── managed-cert.yaml   # SSL certificate configuration
│   └── manifest.yaml       # Main deployment
└── ingress.yaml            # Global ingress configuration

```

## ⚠️ Important: Configuration Placeholders ⚠️

All sensitive information and configurable values have been replaced with environment variable style placeholders. These placeholders must be substituted with actual values before applying the configurations.

### Types of Placeholders

1. **Sensitive Information** (in secrets/):
   - Database credentials: `${POSTGRES_USER}`, `${POSTGRES_PASSWORD}`
   - API keys: `${GOOGLE_MAPS_API_KEY}`
   - Email configuration: `${SMTP_USERNAME}`, `${SMTP_PASSWORD}`

2. **Infrastructure Configuration** (in configs/):
   - Service ports: `${API_GATEWAY_PORT}`, `${NOTIFICATION_API_PORT}`
   - Hostnames: `${DOMAIN_NAME}`
   - URLs: `${CLIENT_ADDRESS}`, `${WS_URL}`

3. **GCP-specific Values**:
   - Registry URL: `${_REGISTRY_URL}`
   - Project ID: `${PROJECT_ID}`
   - Region: `${GCP_REGION}`

### Configuration Files Location

- Backend configurations: `core-api/config/backend.yaml`
- Frontend configurations: `ui/config/frontend.yaml`
- Backend secrets: `core-api/secrets/`
- Frontend secrets: `ui/secrets/`
- SSL certificate: `ui/managed-cert.yaml`
- Ingress configuration: `ingress.yaml`

## Deployment Architecture

The Kubernetes configuration is set up to deploy a microservices architecture that includes:

1. Frontend application (Vue.js/Nuxt.js)
2. Backend services:
   - API Gateway
   - User API
   - Photo API
   - Notification API (WebSockets and Email)
   - GeoJSON Generator
   - Supercluster API (Node.js)
3. Databases:
   - PostgreSQL
   - MongoDB for GeoJSON data
4. Messaging:
   - Kafka
   - Zookeeper
5. Caching:
   - Redis

## GCP Configuration

The following GCP-specific values are managed as environment variables and should be provided through your CI/CD pipeline or local development environment. Do not commit these values to version control:

- `${PROJECT_ID}`: Your Google Cloud project ID
- `${_REGISTRY_URL}`: Full container registry URL (e.g., europe-west1-docker.pkg.dev)

These variables are used in container image paths and other GCP-specific configurations.

## Important Notes

- All configuration files contain placeholders that must be substituted with actual values before deployment
- Environment variable substitution should be performed as part of your deployment process
- Never commit actual values to version control

## Automated Deployment

The deployment was automated through Google Cloud Build:

1. **Code Push Triggers Build**
   - Automatic trigger on push to master branch

2. **Cloud Build Process**
   - Builds Docker images
   - Deploys to GKE cluster

For manual deployments (not recommended), you can use:
```bash
# Example of manual secret substitution (for development only)
export $(cat .env | xargs) && envsubst < secrets/mongodb.yaml | kubectl apply -f -
```

---

These manifests are provided as a reference for Kubernetes configuration and are not currently used in production.