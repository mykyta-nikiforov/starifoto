# 🌍 PhotoMap Core API

This guide covers the setup and deployment of the PhotoMap Core API services.

## 🐳 Current Deployment (Docker on DigitalOcean)

For detailed Docker setup instructions, please refer to the [Docker README](./docker/README.md).

### Prerequisites
- Docker and Docker Compose installed
- Access to DigitalOcean droplet
- MongoDB instance

### Docker Setup
```bash
# Start all services
cd docker
docker-compose -p photomap up -d

# Start a specific service
docker-compose -p photomap up -d notification-api
```

### MongoDB Setup
1. Set proper permissions for MongoDB keyfile:
   ```bash
   chmod 400 mongodb-keyfile
   ```
2. After initial MongoDB initialization, run:
   ```bash
   ./startReplicaSetEnvironment.sh
   ```

### Database Configuration
- Set MongoDB max connections: 200

### Useful Commands
```bash
# Get MongoDB credentials
kubectl get secret <mongodb-secret-name> -n default \
  -o json | jq -r '.data | with_entries(.value |= @base64d)'

# Port forward MongoDB (for local development)
kubectl port-forward $(kubectl get pod --selector="app=photomap-mongodb-svc" --output jsonpath='{.items[0].metadata.name}') 27017:27017
```

---

## 🏗️ Legacy GCP Configuration (Archive)

> Note: This section is kept for reference purposes. The application has been migrated to DigitalOcean.

### Prerequisites
- Google Cloud Platform account with required permissions
- `kubectl` and `gcloud` CLI tools installed
- Docker installed for local development

### GCP Cluster Setup
1. Create a new GKE cluster in Google Cloud Console:
   - Enable Workload Identity
   - Configure service account for the cluster

2. Connect to the cluster:
   ```bash
   gcloud container clusters get-credentials <cluster-name> --zone=<zone>
   kubectl config set-context <cluster-name>
   kubectl config current-context
   ```

### MongoDB Operator Setup
Follow the [official MongoDB Kubernetes Operator guide](https://github.com/mongodb/mongodb-kubernetes-operator/tree/v0.9.0)

1. Install the operator:
   ```bash
   cd k8s/mongo/mongodb-kubernetes-operator
   kubectl apply -f config/crd/bases/mongodbcommunity.mongodb.com_mongodbcommunity.yaml
   kubectl apply -k config/rbac/
   kubectl create -f config/manager/manager.yaml
   ```

2. Create MongoDB secrets:
   ```bash
   kubectl create secret generic mongodb-secret \
     --from-literal=username=<username> \
     --from-literal=password=<password>
   ```

### 🔐 Service Account Configuration
1. Create Kubernetes service account:
   ```bash
   kubectl create serviceaccount k8s-service-account --namespace default
   ```

2. Configure Workload Identity (once per project):
   ```bash
   gcloud iam service-accounts add-iam-policy-binding \
     --role roles/iam.workloadIdentityUser \
     --member "serviceAccount:<project-id>.svc.id.goog[default/k8s-service-account]" \
     <service-account>@<project-id>.iam.gserviceaccount.com
   ```

3. Annotate service account:
   ```bash
   kubectl annotate serviceaccount k8s-service-account \
     --namespace default \
     iam.gke.io/gcp-service-account=<service-account>@<project-id>.iam.gserviceaccount.com
   ```

### Cloud SQL Configuration
1. Connect GKE to Cloud SQL following the [official guide](https://cloud.google.com/kubernetes-engine/docs/how-to/workload-identity#authenticating_to)
2. Set SQL Flag: `max_connections = 200`

### Legacy Deployment Commands
```bash
# Scale down all deployments
kubectl scale deploy -n default --replicas=0 --all

# Configure Docker for Artifact Registry
gcloud auth configure-docker <region>-docker.pkg.dev

# Local kubectl authentication
gcloud container clusters get-credentials <cluster-name> --zone <zone> --project <project-id>
```

### 📝 GCP Variables Reference
Replace these placeholders with your actual values:
- `<cluster-name>`: Your GKE cluster name
- `<zone>`: GCP zone (e.g., europe-west1-b)
- `<region>`: GCP region (e.g., europe-west1)
- `<project-id>`: Your GCP project ID
- `<service-account>`: GCP service account name
- `<username>`: MongoDB username
- `<password>`: MongoDB password
- `<mongodb-secret-name>`: Name of your MongoDB secret in Kubernetes

---
Made with ❤️ for Ukraine 🇺🇦