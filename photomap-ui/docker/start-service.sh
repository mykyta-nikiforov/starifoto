#!/bin/bash
set -e

# Colors for pretty output
GREEN='\033[0;32m'
NC='\033[0m'

echo -e "${GREEN}🔨 Building UI...${NC}"
cd ..

export NUXT_TELEMETRY_DISABLED=1
# Install dependencies and build
npm install

# Create Docker image
echo -e "${GREEN}Creating Docker image for UI...${NC}"
docker build -t "photomap-nuxt-ui:latest" .

# Return to docker directory
cd docker

echo -e "${GREEN}Starting UI...${NC}"
docker compose -p starifoto up -d