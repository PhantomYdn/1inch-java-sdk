#!/bin/bash

# 1inch MCP Server - HTTP/SSE Transport Mode
# Runs the MCP server with HTTP/SSE transport for web applications and remote access

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Default configuration
PORT="${MCP_PORT:-8080}"
HOST="${MCP_HOST:-0.0.0.0}"
PROFILE="${MCP_PROFILE:-http,dev}"

echo "Starting 1inch MCP Server in HTTP/SSE mode..."
echo "This mode supports:"
echo "  • Web applications and browsers"
echo "  • Remote MCP client connections" 
echo "  • Server-Sent Events (SSE) transport"
echo "  • Streamable HTTP transport"
echo ""
echo "Server will be available at:"
echo "  • SSE endpoint: http://$HOST:$PORT/mcp/sse"
echo "  • HTTP endpoint: http://$HOST:$PORT/mcp/messages"
echo "  • Server info: http://$HOST:$PORT/mcp/info"
echo "  • Health check: http://$HOST:$PORT/mcp/health"
echo ""

# Check if JAR exists
JAR_FILE="$PROJECT_DIR/target/oneinch-sdk-mcp-1.0-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "JAR file not found. Building project..."
    cd "$PROJECT_DIR"
    mvn clean package -DskipTests
fi

# Check for API key
if [ -z "$ONEINCH_API_KEY" ]; then
    echo "WARNING: ONEINCH_API_KEY environment variable is not set"
    echo "Please set your 1inch API key:"
    echo "export ONEINCH_API_KEY=your_api_key_here"
    echo ""
fi

# Security warning for production
if [[ "$PROFILE" == *"prod"* ]] && [ -z "$MCP_API_KEY" ]; then
    echo "ERROR: MCP_API_KEY environment variable is required for production mode"
    echo "Set a secure API key for client authentication:"
    echo "export MCP_API_KEY=your_secure_mcp_api_key"
    exit 1
fi

# Show security status
if [[ "$PROFILE" == *"dev"* ]]; then
    echo "Security: DISABLED (development mode)"
else
    echo "Security: ENABLED"
    if [ -n "$MCP_API_KEY" ]; then
        echo "API Key: Configured (required for client connections)"
    else
        echo "API Key: Not configured (clients may be rejected)"
    fi
fi

echo ""
echo "Starting server..."

# Run with HTTP profile
java \
    -Dquarkus.profile="$PROFILE" \
    -Dquarkus.http.host="$HOST" \
    -Dquarkus.http.port="$PORT" \
    -jar "$JAR_FILE"

echo ""
echo "1inch MCP Server stopped."