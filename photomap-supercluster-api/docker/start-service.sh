#!/bin/bash
set -e

# Colors for pretty output
GREEN='\033[0;32m'
NC='\033[0m'

echo -e "${GREEN}🔨 Building Supercluster API...${NC}"
cd ..

# Install dependencies and build
npm install

# Create Docker image
echo -e "${GREEN}Creating Docker image for Supercluster API...${NC}"
docker build -t "supercluster-api:latest" .

# Return to docker directory
cd docker

echo -e "${GREEN}Starting Supercluster API...${NC}"
docker compose -p starifoto up -d