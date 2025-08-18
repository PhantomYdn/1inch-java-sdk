# 1inch MCP Server

A **read-only** Model Context Protocol (MCP) server that provides AI-enhanced access to DeFi data through the 1inch ecosystem. Built with Quarkus for high-performance, native compilation, and enterprise-grade reliability.

## Executive Summary

The 1inch MCP Server bridges the gap between AI applications and DeFi data, enabling intelligent analysis and decision-making without the risks associated with transaction execution. This server transforms the comprehensive 1inch API ecosystem into AI-friendly resources, tools, and prompts that can be consumed by any MCP-compatible client.

**Key Value Propositions:**
- **Safe & Read-Only**: No transaction signing, no private keys, pure information access
- **Comprehensive**: Leverages all 1inch APIs across 13+ blockchain networks
- **AI-Optimized**: Purpose-built for LLM consumption with structured data formats
- **High-Performance**: Quarkus-based with native compilation and intelligent caching
- **Multi-Chain**: Unified access to data across Ethereum, Polygon, BSC, Arbitrum, and more

## Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   MCP Client    │    │  1inch MCP      │    │ oneinch-sdk-core│    │  1inch APIs     │
│  (Claude, etc.) │────│     Server      │────│   (Java SDK)    │────│   (REST)        │
│                 │    │   (Quarkus)     │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Transport Mechanisms
- **Stdio Transport**: For local development and command-line usage
- **HTTP/SSE Transport**: For web applications and remote access (protocol version 2025-03-26)
- **Streamable HTTP**: Modern variant with improved performance

### Security Model
- **Read-Only Operations**: No blockchain transactions or state modifications
- **API Key Management**: Secure handling of 1inch API credentials
- **Rate Limiting**: Intelligent request throttling to respect API limits
- **Data Caching**: Efficient caching to minimize API calls and improve performance

## MCP Components Design

### Resources (Application-Controlled Data Access)

Resources provide direct access to DeFi data with no side effects, similar to REST GET endpoints:

| Resource | Description | Available APIs | Chain Support |
|----------|-------------|----------------|---------------|
| `/tokens/{chainId}` | Token lists and metadata | ✅ Token API | 13+ chains |
| `/prices/{chainId}/{token}` | Real-time token pricing | ✅ Price API | 13+ chains |
| `/portfolio/{address}` | Portfolio snapshots and metrics | ✅ Portfolio API | Multi-chain |
| `/balances/{chainId}/{address}` | Token balances and allowances | ✅ Balance API | 13+ chains |
| `/history/{address}` | Transaction history | ✅ History API | Multi-chain |
| `/swap-routes/{chainId}` | Best swap routes and quotes | ✅ Swap API | 13+ chains |
| `/gas/{chainId}` | Network gas prices | ⚠️ Gas Price API | **SDK Gap** |
| `/charts/{chainId}/{token}` | Price charts and technical data | ⚠️ Charts API | **SDK Gap** |
| `/liquidity/{chainId}` | Liquidity pool information | ✅ Token Details API | 13+ chains |
| `/orderbook/{chainId}` | Limit orders and trading pairs | ✅ Orderbook API | 13+ chains |

### Tools (Model-Controlled Query Functions)

Tools are functions that AI models can invoke to perform analysis and calculations:

#### Core Analysis Tools
- `analyzeSwapRoute(src, dst, amount, chainId)` - Analyze optimal swap paths and costs
- `calculateSlippage(route, amount)` - Calculate expected slippage for trades
- `compareTokenPrices(tokens, chains)` - Cross-chain price comparison
- `getPortfolioMetrics(address, chains)` - Calculate P&L, ROI, and APR metrics

#### Market Intelligence Tools
- `analyzeGasOptimization(chainId, operations)` - Suggest optimal gas strategies
- `searchTokens(query, chains, filters)` - Smart token search with filtering
- `getMarketTrends(tokens, timeframe)` - Analyze price movements and volume

#### Portfolio Management Tools
- `analyzePortfolioRisk(address)` - Assess risk exposure and concentration
- `findYieldOpportunities(tokens, chains)` - Discover DeFi yield strategies
- `compareProtocols(protocols, metrics)` - Protocol performance comparison
- `generateRebalancingStrategy(portfolio, targets)` - Portfolio rebalancing suggestions

#### Advanced Analytics Tools (Require SDK Expansion)
- `predictGasOptimalTimes(chainId)` - Best transaction timing ⚠️ **Needs Gas API**
- `generatePriceCharts(token, timeframe)` - Technical analysis charts ⚠️ **Needs Charts API**

### Prompts (User-Controlled Templates)

Prompts provide optimized templates for common DeFi analysis tasks:

#### Trading & Swaps
- `swap-analysis` - "Analyze the best way to swap {amount} {srcToken} to {dstToken} on {chain}"
- `slippage-check` - "What slippage should I expect for this {amount} {token} trade?"
- `gas-optimization` - "When is the best time to execute transactions on {chain}?"

#### Portfolio Management
- `portfolio-review` - "Analyze portfolio performance for address {address} over {timeframe}"
- `risk-assessment` - "Assess the risk profile of portfolio at {address}"
- `rebalancing-strategy` - "Suggest rebalancing strategy for portfolio targeting {allocation}"

#### Market Analysis
- `market-report` - "Generate daily DeFi market report for {tokens} across {chains}"
- `yield-opportunities` - "Find the best yield farming opportunities for {amount} {token}"
- `price-comparison` - "Compare {token} prices across {chains} and exchanges"

#### Research & Analytics
- `token-research` - "Provide comprehensive analysis of {token} on {chain}"
- `protocol-comparison` - "Compare {protocols} across metrics {metrics}"
- `trend-analysis` - "Analyze price trends for {tokens} over {period}"

## Real-World Use Cases

### 1. AI-Powered Trading Assistant
**Scenario**: Day trader using Claude to optimize DeFi strategies
**Workflow**:
- Query real-time prices across exchanges
- Analyze gas costs for optimal timing
- Compare token prices across chains
- Generate risk assessments

**Example Interaction**:
```
User: "Should I swap 1000 USDC to ETH right now on Arbitrum?"
Claude: [Uses swap-analysis prompt] → [Calls analyzeSwapRoute tool] → [Accesses gas resource]
Response: "Current rate: 1 ETH = 2,450 USDC. Best route through Uniswap V3 with 0.05% slippage. 
Gas cost: $0.50. Alternative: Wait 2 hours for 15% lower gas fees."
```

### 2. Portfolio Analytics Dashboard
**Scenario**: Investment manager monitoring multiple DeFi positions
**Workflow**:
- Track portfolio performance across chains
- Monitor risk exposure
- Generate compliance reports
- Identify rebalancing opportunities

### 3. DeFi Research Platform
**Scenario**: Researcher analyzing protocol efficiency
**Workflow**:
- Compare liquidity across protocols
- Analyze yield strategies
- Track market trends
- Generate analytical reports

### 4. Automated Market Monitoring
**Scenario**: Institutional trader monitoring market conditions
**Workflow**:
- Real-time price alerts
- Liquidity monitoring
- Gas price optimization
- Cross-chain price monitoring

### 5. Educational DeFi Explorer
**Scenario**: Learning about DeFi through AI-guided exploration
**Workflow**:
- Explain token economics
- Demonstrate swap mechanics
- Compare protocol features
- Simulate trading strategies

## API Gap Analysis

### Currently Available (SDK Implemented)
✅ **Swap API** - Quote generation, route analysis, gas estimation
✅ **Token API** - Token lists, search, custom token data (multi-chain + chain-specific)
✅ **Token Details API** - Pricing data, market metrics, charts (chain-specific)
✅ **Portfolio API v5** - DeFi positions, P&L metrics, analytics (multi-chain)
✅ **Balance API** - Token balances, allowances (chain-specific)
✅ **Price API** - Real-time pricing, 60+ currencies (chain-specific)  
✅ **History API** - Transaction history, analytics (multi-chain)
✅ **Orderbook API** - Limit orders, trading pairs (chain-specific)
✅ **Fusion API** - Gasless swaps, auction system (chain-specific)
✅ **FusionPlus API** - Cross-chain gasless swaps (cross-chain)

### SDK Gaps (High Priority for MCP)
⚠️ **Gas Price API** - Real-time gas optimization data (needed for gas analysis tools)
⚠️ **Charts API** - Historical price charts and technical indicators (needed for trend analysis)
⚠️ **Transaction Gateway API** - MEV protection and transaction broadcasting info
⚠️ **Spot Price API** - High-frequency price feeds for algorithmic analysis

### Lower Priority SDK Gaps
⚠️ **Traces API** - Transaction trace analysis and debugging
⚠️ **NFT API v2** - NFT trading and marketplace data
⚠️ **Domains API** - ENS and domain resolution services  
⚠️ **Web3 API** - General blockchain utilities

## Technical Specification

### Technology Stack
- **Framework**: Quarkus 3.x with MCP Server extension
- **Language**: Java 11+
- **SDK Integration**: oneinch-sdk-core
- **Transport**: Stdio, HTTP/SSE, Streamable HTTP
- **Caching**: Redis/Caffeine for performance optimization
- **Monitoring**: Micrometer metrics, OpenTelemetry tracing

### Dependencies
```xml
<dependency>
    <groupId>io.quarkiverse.mcp</groupId>
    <artifactId>quarkus-mcp-server-stdio</artifactId>
</dependency>
<dependency>
    <groupId>io.oneinch.sdk</groupId>
    <artifactId>oneinch-sdk-core</artifactId>
</dependency>
```

### Configuration Structure
```properties
# 1inch API Configuration
oneinch.api.key=${ONEINCH_API_KEY}
oneinch.api.base-url=https://api.1inch.dev

# MCP Server Configuration  
mcp.server.name=1inch-mcp-server
mcp.server.version=1.0.0

# Caching Configuration
mcp.cache.prices.ttl=30s
mcp.cache.tokens.ttl=1h
mcp.cache.portfolio.ttl=5m

# Rate Limiting
mcp.rate-limit.requests-per-minute=60
mcp.rate-limit.burst-capacity=10
```

### Error Handling Strategy
- **API Errors**: Graceful degradation with cached fallbacks
- **Network Issues**: Retry with exponential backoff
- **Rate Limits**: Intelligent queuing and user notification
- **Data Validation**: Schema validation for all responses

## Implementation Plan

### Phase 1: Foundation ✅
- [x] Project setup with Quarkus and MCP dependencies
- [x] Basic MCP server structure with stdio transport
- [x] Configuration management and API key handling
- [x] Rate limiting and caching infrastructure
- [x] Integration with oneinch-sdk-core
- [x] Basic health checks and monitoring

### Phase 2: Core Resources (Data Access)
- [x] `/tokens/{chainId}` resource - Token lists and metadata
- [x] `/prices/{chainId}/{token}` resource - Real-time pricing data
- [x] `/portfolio/{address}` resource - Portfolio snapshots
- [x] `/balances/{chainId}/{address}` resource - Balance information
- [x] `/history/{address}` resource - Transaction history
- [x] `/swap-routes/{chainId}` resource - Swap quote data

### Phase 3: Essential Tools (AI Functions)
- [x] `getSwapQuote` - Generate swap quotes with route analysis
- [x] `analyzeToken` - Comprehensive token analysis
- [x] `getPortfolioValue` - Portfolio valuation and metrics
- [x] `searchTokens` - Multi-chain token search functionality
- [ ] `compareProtocols` - Protocol performance comparison
- [x] `calculateSlippage` - Slippage calculation for trades (implemented as `calculatePriceImpact` in SwapQuoteTool)

### Phase 4: Advanced Analytics Tools  
- [x] `compareCrossChainPrices` - Cross-chain price comparison (integrated in SearchTokensTool)
- [x] `analyzeGasOptimization` - Gas cost optimization strategies
- [x] `getMarketTrends` - Market movement analysis
- [x] `analyzePortfolioRisk` - Risk assessment and diversification (integrated in PortfolioValueTool)
- [x] `findYieldOpportunities` - DeFi yield strategy discovery (integrated in PortfolioValueTool)
- [x] `generateRebalancingStrategy` - Portfolio optimization (integrated in PortfolioValueTool)

### Phase 5: Prompts Implementation
- [x] `swap-analysis` prompt for trade optimization
- [x] `portfolio-review` prompt for performance analysis  
- [x] `market-report` prompt for daily market insights
- [x] `yield-opportunities` prompt for yield farming
- [x] `risk-assessment` prompt for portfolio risk analysis
- [x] `price-comparison` prompt for cross-chain price analysis
- [x] `token-research` prompt for comprehensive token analysis

### Phase 6: SDK Integration Tasks
- [x] Integrate Swap API for route analysis
- [x] Integrate Token API for multi-chain token data
- [x] Integrate Portfolio API for DeFi position tracking
- [x] Integrate Balance API for wallet analysis
- [x] Integrate Price API for real-time pricing
- [x] Integrate History API for transaction analysis
- [ ] Integrate Orderbook API for limit order data
- [ ] Integrate Fusion API for gasless swap info

### Phase 7: Missing API Integration (SDK Expansion Required)
- [ ] Create SDK issues for Gas Price API implementation
- [ ] Create SDK issues for Charts API implementation  
- [ ] Document requirements for Transaction Gateway API
- [ ] Plan integration once APIs are available in SDK

### Phase 8: Transport Layer Enhancements
- [x] HTTP/SSE transport implementation
- [x] Streamable HTTP transport support

### Phase 9: Other
- [ ] Unit tests for all MCP components (resources, tools, prompts)
- [ ] Comprehensive API documentation
- [ ] Usage examples for different MCP clients
- [ ] Docker containerization with multi-stage builds

## Usage Examples

The 1inch MCP Server supports multiple transport modes for different use cases:

### Transport Modes

#### 1. Stdio Transport (Local Development)
Best for local development, Claude Desktop integration, and command-line AI applications.

```bash
# Set your 1inch API key
export ONEINCH_API_KEY=your_api_key_here

# Run in stdio mode
./scripts/run-stdio.sh

# Or with Maven
mvn quarkus:dev -Dquarkus.profile=stdio,dev
```

#### 2. HTTP/SSE Transport (Web Applications)
Ideal for web applications, remote access, and production deployments.

```bash
# Set required environment variables
export ONEINCH_API_KEY=your_api_key_here
export MCP_API_KEY=your_secure_mcp_api_key  # For client authentication

# Run in HTTP mode
./scripts/run-http.sh

# Or with custom port and host
MCP_PORT=9090 MCP_HOST=localhost ./scripts/run-http.sh

# Production mode with security enabled
MCP_PROFILE=http,prod ./scripts/run-http.sh
```

#### 3. Docker Deployment (Production)
For containerized deployments and cloud platforms.

```bash
# Set required environment variables
export ONEINCH_API_KEY=your_api_key_here
export MCP_API_KEY=your_secure_mcp_api_key

# Deploy with Docker
./scripts/run-docker.sh

# Custom configuration
MCP_PORT=8080 MCP_CONTAINER_NAME=my-mcp-server ./scripts/run-docker.sh
```

### Client Integration Examples

#### Claude Desktop Integration (Stdio)
```bash
# Install via JBang (when available)
jbang 1inch-mcp-server@io.oneinch.mcp

# Configure Claude with MCP server
claude config add-mcp-server 1inch-mcp-server stdio jbang 1inch-mcp-server@io.oneinch.mcp
```

**Example Conversation:**
```
User: "What's the best way to swap 1000 USDC to WETH on Polygon?"

Claude: I'll analyze the best swap route for you.
[Uses swap-analysis prompt and analyzeSwapRoute tool]

The optimal route for swapping 1000 USDC to WETH on Polygon:
- Route: USDC → WETH via Uniswap V3 (0.05% fee)
- Expected output: ~0.408 WETH  
- Slippage: 0.03% (very low)
- Gas cost: ~$0.02
- Total cost: ~$2.50 (including DEX fees)

Alternative routes:
1. QuickSwap: 0.407 WETH (-0.2% worse)
2. SushiSwap: 0.406 WETH (-0.5% worse)

Recommendation: Use Uniswap V3 route for best price execution.
```

#### Web Application Integration (HTTP/SSE)
```javascript
// Connect to MCP server via SSE
const eventSource = new EventSource('http://localhost:8080/mcp/sse', {
  headers: {
    'X-API-Key': 'your_mcp_api_key'
  }
});

eventSource.onmessage = function(event) {
  const data = JSON.parse(event.data);
  console.log('MCP Data:', data);
};

// Or use Streamable HTTP (recommended)
async function callMcpTool(toolName, params) {
  const response = await fetch('http://localhost:8080/mcp/messages', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-API-Key': 'your_mcp_api_key'
    },
    body: JSON.stringify({
      jsonrpc: '2.0',
      id: Date.now(),
      method: 'tools/call',
      params: {
        name: toolName,
        arguments: params
      }
    })
  });
  return response.json();
}

// Example: Get swap quote
const swapQuote = await callMcpTool('getSwapQuote', {
  chainId: 137,
  srcToken: '0x2791bca1f2de4661ed88a30c99a7a9449aa84174', // USDC
  dstToken: '0x7ceb23fd6f88dc98aa049b4c9c4bd308e8bb9c79', // WETH  
  amount: '1000000000' // 1000 USDC in wei
});
```

#### Cursor IDE Integration
**Stdio Mode:**
```json
{
  "mcp": {
    "servers": {
      "1inch": {
        "command": "jbang",
        "args": ["1inch-mcp-server@io.oneinch.mcp"],
        "env": {
          "ONEINCH_API_KEY": "${ONEINCH_API_KEY}"
        }
      }
    }
  }
}
```

**HTTP Mode:**
```json
{
  "mcp": {
    "servers": {
      "1inch": {
        "transport": {
          "type": "sse",
          "url": "http://localhost:8080/mcp/sse",
          "headers": {
            "X-API-Key": "${MCP_API_KEY}"
          }
        }
      }
    }
  }
}
```

#### Python Client Example
```python
import asyncio
import aiohttp
import json

class OneInchMcpClient:
    def __init__(self, base_url="http://localhost:8080/mcp", api_key=None):
        self.base_url = base_url
        self.api_key = api_key
        self.headers = {
            'Content-Type': 'application/json',
            'X-API-Key': api_key
        } if api_key else {'Content-Type': 'application/json'}
    
    async def call_tool(self, tool_name, **params):
        """Call an MCP tool via HTTP"""
        async with aiohttp.ClientSession() as session:
            payload = {
                "jsonrpc": "2.0",
                "id": 1,
                "method": "tools/call",
                "params": {
                    "name": tool_name,
                    "arguments": params
                }
            }
            
            async with session.post(
                f"{self.base_url}/messages",
                headers=self.headers,
                json=payload
            ) as response:
                return await response.json()
    
    async def get_swap_quote(self, chain_id, src_token, dst_token, amount):
        """Get a swap quote using the MCP server"""
        return await self.call_tool(
            "getSwapQuote",
            chainId=chain_id,
            srcToken=src_token,
            dstToken=dst_token,
            amount=str(amount)
        )

# Usage example
async def main():
    client = OneInchMcpClient(api_key="your_mcp_api_key")
    
    quote = await client.get_swap_quote(
        chain_id=1,
        src_token="0xA0b86a33E6411d80acCB3dEfcb0b47A4Caaa5B18",  # USDC
        dst_token="0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2",  # WETH
        amount=1000_000_000  # 1000 USDC
    )
    
    print(json.dumps(quote, indent=2))

if __name__ == "__main__":
    asyncio.run(main())
```

### API Testing and Development

#### Test Server Health
```bash
# Check if server is running
curl http://localhost:8080/mcp/health

# Get server information
curl http://localhost:8080/mcp/info

# Test with authentication (if enabled)
curl -H "X-API-Key: your_mcp_api_key" \
     http://localhost:8080/mcp/info
```

#### Test SSE Connection
```bash
# Connect to SSE endpoint
curl -H "Accept: text/event-stream" \
     -H "X-API-Key: your_mcp_api_key" \
     http://localhost:8080/mcp/sse
```

#### Test HTTP Messages
```bash
# Send MCP request via HTTP
curl -X POST http://localhost:8080/mcp/messages \
     -H "Content-Type: application/json" \
     -H "X-API-Key: your_mcp_api_key" \
     -d '{
       "jsonrpc": "2.0",
       "id": 1,
       "method": "tools/call",
       "params": {
         "name": "getSwapQuote",
         "arguments": {
           "chainId": 1,
           "srcToken": "0xA0b86a33E6411d80acCB3dEfcb0b47A4Caaa5B18",
           "dstToken": "0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2", 
           "amount": "1000000000"
         }
       }
     }'
```

## Security Considerations

### Transport Security

#### Stdio Transport
- **Local only**: Communication stays on local machine
- **No network exposure**: Inherently secure for single-user scenarios
- **Process isolation**: Runs as local application process

#### HTTP/SSE Transport  
- **Authentication required**: Always use API key authentication in production
- **HTTPS recommended**: Use reverse proxy (nginx, Apache) for TLS termination
- **CORS configuration**: Restrict origins in production environments
- **Network security**: Firewall and network-level access controls

### Authentication Methods

#### API Key Authentication (Recommended)
```bash
# Set secure API key for production
export MCP_API_KEY=mcp_live_1234567890abcdef...

# Clients must include header:
# X-API-Key: mcp_live_1234567890abcdef...
```

#### JWT Authentication (Advanced)
```yaml
# application.yml
mcp:
  transport:
    security:
      auth-method: JWT
      jwt:
        issuer: https://your-auth-provider.com
        audience: 1inch-mcp-server
        jwks-url: https://your-auth-provider.com/.well-known/jwks.json
```

### Production Deployment Checklist

- [ ] **API Keys**: Use strong, unique API keys for MCP authentication
- [ ] **1inch API Key**: Secure 1inch API key storage (environment variables)
- [ ] **HTTPS**: Deploy behind reverse proxy with TLS termination
- [ ] **CORS**: Restrict allowed origins to specific domains
- [ ] **Monitoring**: Enable metrics and health checks
- [ ] **Logging**: Configure structured logging for audit trails
- [ ] **Rate Limiting**: Configure appropriate rate limits per client
- [ ] **Network**: Use firewalls and security groups to restrict access
- [ ] **Updates**: Keep dependencies and base images updated

### Environment Variables

#### Required
```bash
ONEINCH_API_KEY=your_1inch_api_key_here    # 1inch API access
MCP_API_KEY=your_mcp_api_key_here          # Client authentication (HTTP mode)
```

#### Optional Security
```bash
MCP_USERNAME=admin                          # Basic auth username
MCP_PASSWORD=secure_password               # Basic auth password
QUARKUS_PROFILE=prod                       # Production profile
```

## Monitoring & Observability

### Metrics Collection
- Request/response latencies
- API error rates and types
- Cache hit/miss ratios
- Memory and CPU usage

### Logging Strategy
- Structured JSON logging
- Request correlation IDs
- Error stack traces
- Performance debugging logs

### Health Checks
- 1inch API connectivity
- Cache system status
- Memory usage monitoring  
- Response time thresholds

## Future Roadmap

### Short Term (Q2 2025)
- [ ] Core MCP server implementation
- [ ] Integration with available SDK APIs
- [ ] Basic Claude/Cursor compatibility
- [ ] Documentation and examples

### Medium Term (Q3 2025)  
- [ ] Advanced analytics tools
- [ ] Real-time WebSocket updates
- [ ] Performance optimizations
- [ ] Extended MCP client support

### Long Term (Q4 2025+)
- [ ] Machine learning insights
- [ ] Predictive analytics
- [ ] Advanced portfolio optimization
- [ ] Integration with expanded SDK APIs

## Contributing

We welcome contributions to the 1inch MCP Server project! Areas where help is needed:

### High Priority
- **SDK API Integration**: Help implement missing APIs in the core SDK
- **MCP Tool Development**: Create new analysis tools and resources  
- **Performance Optimization**: Improve caching and request handling
- **Documentation**: Improve examples and usage guides

### Medium Priority  
- **Testing**: Add comprehensive test coverage
- **Client Integration**: Test with various MCP clients
- **Deployment**: Create deployment templates and guides
- **Monitoring**: Enhanced observability and metrics

### Getting Started
1. Fork the repository
2. Set up development environment with Quarkus
3. Add your 1inch API key to environment variables
4. Run tests to ensure setup works
5. Pick an issue labeled `good-first-issue`
6. Submit pull request with tests and documentation

For detailed development setup and contribution guidelines, see [CONTRIBUTING.md](CONTRIBUTING.md).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- **GitHub Issues**: [1inch SDK MCP Issues](https://github.com/1inch/1inch-java-sdk/issues)
- **1inch Developer Portal**: [https://portal.1inch.dev/](https://portal.1inch.dev/)
- **MCP Documentation**: [https://modelcontextprotocol.io/](https://modelcontextprotocol.io/)
- **Quarkus MCP Guide**: [https://quarkus.io/guides/mcp](https://quarkus.io/guides/mcp)

---

**Built with ❤️ for the DeFi and AI communities**

*Bringing intelligent analysis to decentralized finance through the power of AI and the Model Context Protocol.*
