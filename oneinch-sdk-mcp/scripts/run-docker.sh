#!/bin/bash

# 1inch MCP Server - Docker Deployment
# Builds and runs the MCP server in a Docker container

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Configuration
IMAGE_NAME="${MCP_IMAGE_NAME:-oneinch-mcp-server}"
CONTAINER_NAME="${MCP_CONTAINER_NAME:-oneinch-mcp}"
PORT="${MCP_PORT:-8080}"
PROFILE="${MCP_PROFILE:-http,prod}"

echo "1inch MCP Server - Docker Deployment"
echo "===================================="
echo ""
echo "Configuration:"
echo "  • Image: $IMAGE_NAME"
echo "  • Container: $CONTAINER_NAME"
echo "  • Port: $PORT"
echo "  • Profile: $PROFILE"
echo ""

# Check for required environment variables
if [ -z "$ONEINCH_API_KEY" ]; then
    echo "ERROR: ONEINCH_API_KEY environment variable is required"
    echo "export ONEINCH_API_KEY=your_api_key_here"
    exit 1
fi

if [ -z "$MCP_API_KEY" ]; then
    echo "ERROR: MCP_API_KEY environment variable is required for container security"
    echo "export MCP_API_KEY=your_secure_mcp_api_key"
    exit 1
fi

# Stop existing container
if docker ps -a --format 'table {{.Names}}' | grep -q "^$CONTAINER_NAME$"; then
    echo "Stopping existing container..."
    docker stop "$CONTAINER_NAME" >/dev/null 2>&1 || true
    docker rm "$CONTAINER_NAME" >/dev/null 2>&1 || true
fi

# Check if Dockerfile exists
DOCKERFILE="$PROJECT_DIR/Dockerfile"
if [ ! -f "$DOCKERFILE" ]; then
    echo "Creating Dockerfile..."
    cat > "$DOCKERFILE" << 'EOF'
FROM eclipse-temurin:11-jre-alpine

LABEL org.opencontainers.image.source="https://github.com/1inch/1inch-java-sdk"
LABEL org.opencontainers.image.description="1inch MCP Server - AI-enhanced DeFi data access"
LABEL org.opencontainers.image.licenses="MIT"

# Install required packages
RUN apk add --no-cache curl

# Create app user
RUN addgroup -S app && adduser -S app -G app

# Set working directory
WORKDIR /app

# Copy the JAR file
COPY target/oneinch-sdk-mcp-*.jar app.jar

# Change ownership
RUN chown -R app:app /app

# Switch to app user
USER app

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/mcp/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
    echo "Dockerfile created."
fi

# Build the application if needed
JAR_FILE="$PROJECT_DIR/target/oneinch-sdk-mcp-1.0-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "Building application..."
    cd "$PROJECT_DIR"
    mvn clean package -DskipTests
fi

# Build Docker image
echo "Building Docker image..."
cd "$PROJECT_DIR"
docker build -t "$IMAGE_NAME" .

# Run container
echo "Starting container..."
docker run -d \
    --name "$CONTAINER_NAME" \
    --restart unless-stopped \
    -p "$PORT:8080" \
    -e ONEINCH_API_KEY="$ONEINCH_API_KEY" \
    -e MCP_API_KEY="$MCP_API_KEY" \
    -e QUARKUS_PROFILE="$PROFILE" \
    "$IMAGE_NAME"

echo ""
echo "Container started successfully!"
echo ""
echo "Endpoints:"
echo "  • SSE: http://localhost:$PORT/mcp/sse"
echo "  • HTTP: http://localhost:$PORT/mcp/messages"  
echo "  • Info: http://localhost:$PORT/mcp/info"
echo "  • Health: http://localhost:$PORT/mcp/health"
echo ""
echo "Container logs:"
docker logs -f "$CONTAINER_NAME"