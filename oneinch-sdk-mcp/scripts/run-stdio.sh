#!/bin/bash

# 1inch MCP Server - Stdio Transport Mode
# Runs the MCP server with stdio transport for local development and CLI integration

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "Starting 1inch MCP Server in STDIO mode..."
echo "This mode is suitable for:"
echo "  • Local development and testing"
echo "  • Claude Desktop integration"
echo "  • Command-line AI applications"
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
    read -p "Continue without API key? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Run with stdio profile
echo "Running MCP server with stdio transport..."
java -jar "$JAR_FILE" -Dquarkus.profile=stdio,dev

echo ""
echo "1inch MCP Server stopped."