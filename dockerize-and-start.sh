#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Function to check if .env files exist
check_env_files() {
    local service_dir=$1
    if [ ! -f "$service_dir/docker/.env" ]; then
        if [ -f "$service_dir/docker/.env.template" ]; then
            echo "⚠️  Warning: $service_dir/docker/.env not found. Creating from template..."
            cp "$service_dir/docker/.env.template" "$service_dir/docker/.env"
            echo "Please update $service_dir/docker/.env with your configuration"
        else
            echo "❌ Error: Neither .env nor .env.template found in $service_dir/docker/"
            exit 1
        fi
    fi
}

# Function to make script executable
make_executable() {
    local script_path=$1
    if [ ! -x "$script_path" ]; then
        chmod +x "$script_path"
    fi
}

# Colors for pretty output
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Starting local development environment setup...${NC}"

# Check and setup environment files
echo -e "\n${BLUE}📝 Checking environment files...${NC}"
check_env_files "photomap-core-api"
check_env_files "photomap-supercluster-api"
check_env_files "photomap-ui"

# Make service scripts executable
make_executable "photomap-core-api/docker/start-service.sh"
make_executable "photomap-supercluster-api/docker/start-service.sh"
make_executable "photomap-ui/docker/start-service.sh"

# Start Core API services
echo -e "\n${BLUE}🚀 Starting Core API services...${NC}"
cd photomap-core-api/docker
./start-service.sh
cd ../..

# Start Supercluster API
echo -e "\n${BLUE}🚀 Starting Supercluster API...${NC}"
cd photomap-supercluster-api/docker
./start-service.sh
cd ../..

# Start UI
echo -e "\n${BLUE}🚀 Starting UI...${NC}"
cd photomap-ui/docker
./start-service.sh
cd ../..

echo -e "\n${BLUE}✨ All services are up and running!${NC}"
echo -e "📝 Please check the following URLs:"
echo -e "- UI: http://localhost:5173"
echo -e "- API Gateway: http://localhost:8081"
echo -e "- Supercluster API: http://localhost:3000"

echo -e "\n${BLUE}💡 Useful commands:${NC}"
echo "- View logs: docker compose -p starifoto logs -f [service-name]"
echo "- Stop all: docker compose -p starifoto down" 