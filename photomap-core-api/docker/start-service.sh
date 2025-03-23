#!/bin/bash
set -e

# Colors for pretty output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Function to check and generate MongoDB keyfile
check_mongo_keyfile() {
    local keyfile_path="mongo/mongodb-keyfile"
    if [ ! -f "$keyfile_path" ]; then
        echo -e "${YELLOW}⚠️  MongoDB keyfile not found, generating a new one...${NC}"
        # Generate a secure keyfile
        openssl rand -base64 756 > "$keyfile_path"
        chmod 400 "$keyfile_path"
        echo -e "${GREEN}✓ Generated new MongoDB keyfile${NC}"
    else
        # Check if permissions are correct (400 in octal)
        local perms
        if [[ "$OSTYPE" == "darwin"* ]]; then
            perms=$(stat -f %Lp "$keyfile_path")
        else
            perms=$(stat -c %a "$keyfile_path")
        fi
        
        if [ "$perms" != "400" ]; then
            echo -e "${YELLOW}⚠️  Fixing MongoDB keyfile permissions...${NC}"
            chmod 400 "$keyfile_path"
            echo -e "${GREEN}✓ Fixed keyfile permissions${NC}"
        fi
    fi
}

# Check Java version
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" != "17" ]; then
    echo -e "${RED}❌ Error: Project requires Java 17, but Java $java_version is being used${NC}"
    echo -e "${YELLOW}Please set Java 17 as your default Java version or set JAVA_HOME to Java 17 installation${NC}"
    exit 1
fi

# Check and generate MongoDB keyfile
check_mongo_keyfile

echo -e "${GREEN}🔨 Building Core API microservices...${NC}"
cd ..

# Build and publish shared library
echo -e "${GREEN}Building photomap-common...${NC}"
cd photomap-common
./gradlew clean build publishToMavenLocal --refresh-dependencies
cd ..

# Build microservices
microservices=("api-gateway" "geojson-generator" "photo-api" "user-api" "notification-api")
for ms in "${microservices[@]}"; do
    echo -e "${GREEN}Building $ms...${NC}"
    cd "$ms"
    ./gradlew clean build bootJar --refresh-dependencies
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Failed to build $ms${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}Creating Docker image for $ms...${NC}"
    docker build -t "$ms:latest" .
    cd ..
done

# Return to docker directory
cd docker

echo -e "${GREEN}Starting Core API services...${NC}"
docker compose -p starifoto up -d