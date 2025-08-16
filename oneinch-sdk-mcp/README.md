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
| `/prices/{chainId}` | Real-time token pricing | ✅ Price API | 13+ chains |
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

### Phase 1: Foundation
- [x] Project setup with Quarkus and MCP dependencies
- [x] Basic MCP server structure with stdio transport
- [ ] Configuration management and API key handling
- [ ] Rate limiting and caching infrastructure
- [ ] Integration with oneinch-sdk-core
- [ ] Basic health checks and monitoring

### Phase 2: Core Resources (Data Access)
- [ ] `/tokens/{chainId}` resource - Token lists and metadata
- [ ] `/prices/{chainId}/{token}` resource - Real-time pricing data
- [ ] `/portfolio/{address}` resource - Portfolio snapshots
- [ ] `/balances/{chainId}/{address}` resource - Balance information
- [ ] `/history/{address}` resource - Transaction history
- [ ] `/swap-routes/{chainId}` resource - Swap quote data

### Phase 3: Essential Tools (AI Functions)
- [ ] `getSwapQuote` - Generate swap quotes with route analysis
- [ ] `analyzeToken` - Comprehensive token analysis
- [ ] `getPortfolioValue` - Portfolio valuation and metrics
- [ ] `searchTokens` - Multi-chain token search functionality
- [ ] `compareProtocols` - Protocol performance comparison
- [ ] `calculateSlippage` - Slippage calculation for trades

### Phase 4: Advanced Analytics Tools  
- [ ] `compareCrossChainPrices` - Cross-chain price comparison
- [ ] `analyzeGasOptimization` - Gas cost optimization strategies
- [ ] `getMarketTrends` - Market movement analysis
- [ ] `analyzePortfolioRisk` - Risk assessment and diversification
- [ ] `findYieldOpportunities` - DeFi yield strategy discovery
- [ ] `generateRebalancingStrategy` - Portfolio optimization

### Phase 5: Prompts Implementation
- [ ] `swap-analysis` prompt for trade optimization
- [ ] `portfolio-review` prompt for performance analysis  
- [ ] `market-report` prompt for daily market insights
- [ ] `yield-opportunities` prompt for yield farming
- [ ] `risk-assessment` prompt for portfolio risk analysis
- [ ] `price-comparison` prompt for cross-chain price analysis

### Phase 6: SDK Integration Tasks
- [ ] Integrate Swap API for route analysis
- [ ] Integrate Token API for multi-chain token data
- [ ] Integrate Portfolio API for DeFi position tracking
- [ ] Integrate Balance API for wallet analysis
- [ ] Integrate Price API for real-time pricing
- [ ] Integrate History API for transaction analysis
- [ ] Integrate Orderbook API for limit order data
- [ ] Integrate Fusion API for gasless swap info

### Phase 7: Missing API Integration (SDK Expansion Required)
- [ ] Create SDK issues for Gas Price API implementation
- [ ] Create SDK issues for Charts API implementation  
- [ ] Document requirements for Transaction Gateway API
- [ ] Plan integration once APIs are available in SDK

### Phase 8: Transport Layer Enhancements
- [ ] HTTP/SSE transport implementation
- [ ] Streamable HTTP transport support
- [ ] WebSocket support for real-time updates
- [ ] Authentication and authorization for HTTP transports
- [ ] CORS configuration for web clients

### Phase 9: Performance & Reliability
- [ ] Intelligent caching strategy implementation
- [ ] Connection pooling and HTTP client optimization
- [ ] Circuit breaker pattern for API resilience
- [ ] Metrics collection and monitoring dashboards
- [ ] Load testing and performance optimization
- [ ] Memory usage optimization for native builds

### Phase 10: Testing & Quality Assurance
- [ ] Unit tests for all MCP components (resources, tools, prompts)
- [ ] Integration tests with real 1inch APIs
- [ ] MCP client compatibility testing (Claude, Cursor, etc.)
- [ ] Performance benchmarking and load testing
- [ ] Security audit and penetration testing
- [ ] Documentation review and validation

### Phase 11: Documentation & Examples
- [ ] Comprehensive API documentation
- [ ] Usage examples for different MCP clients
- [ ] Configuration guides for various deployment scenarios
- [ ] Troubleshooting guide and FAQ
- [ ] Video tutorials and demos
- [ ] Integration guides for popular AI platforms

### Phase 12: Deployment & Distribution  
- [ ] Docker containerization with multi-stage builds
- [ ] Native image compilation with GraalVM
- [ ] JBang packaging for easy distribution
- [ ] Cloud deployment templates (AWS, GCP, Azure)
- [ ] MCP registry submission and publication
- [ ] CI/CD pipeline setup for automated releases

### Phase 13: Community & Ecosystem
- [ ] GitHub repository setup with issue templates
- [ ] Contributing guidelines and development setup
- [ ] Community Discord/discussions setup
- [ ] Blog posts and technical articles
- [ ] Conference presentations and demos
- [ ] Partnership discussions with MCP ecosystem

## Usage Examples

### Claude Integration
```bash
# Install via JBang
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

### Cursor IDE Integration
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

### Standalone HTTP Server
```bash
# Start HTTP server
java -jar 1inch-mcp-server.jar

# Access via HTTP/SSE
curl -H "Authorization: Bearer <token>" \
     http://localhost:8080/mcp/sse
```

## Security Considerations

### API Key Management
- Environment variable configuration
- Secure storage recommendations  
- Rotation procedures
- Access logging

### Rate Limiting
- Per-client rate limits
- Burst capacity management
- Fair usage policies
- Abuse prevention

### Data Privacy
- No sensitive data storage
- Audit logging capabilities
- GDPR compliance considerations
- Data retention policies

### Network Security
- HTTPS enforcement for HTTP transport
- WebSocket security best practices
- CORS policy configuration
- DDoS protection strategies

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