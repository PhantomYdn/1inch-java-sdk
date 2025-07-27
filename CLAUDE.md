# 1inch Java SDK Development Guide

## Project Overview
Java SDK for the 1inch DEX Aggregation Protocol with Java 11 compatibility.

## Tech Stack
- **Java Version**: 11
- **Build System**: Maven
- **HTTP Client**: OkHttp 4.11.0
- **API Framework**: Retrofit 2.9.0
- **Reactive Programming**: RxJava 3.1.6
- **JSON Processing**: Jackson 2.15.2
- **Logging**: SLF4J 1.7.36
- **Boilerplate Reduction**: Lombok 1.18.30
- **Testing**: JUnit 5.9.3 + Mockito 4.11.0

## Project Structure
```
src/main/java/io/oneinch/sdk/
├── client/          # HTTP client implementations
├── model/           # API request/response models
├── service/         # Service interfaces and implementations
├── exception/       # Custom exceptions
└── util/           # Utility classes
```

## Key Requirements
1. Interface-driven design (easy to mock)
2. Multiple programming approaches: reactive (RxJava), synchronous, and asynchronous (CompletableFuture)
3. Support for both simple blocking calls and complex reactive workflows
4. Proper error handling and logging
5. Well-documented code with Javadoc
6. Unit tests with JUnit 5
7. API_KEY authentication required

## 1inch API Endpoints

### Swap API (Chain-Specific) 
ChainId is required as path parameter - each chain has separate endpoints:
- **Base URL**: `https://api.1inch.dev/`
- **Quote**: `GET /swap/v6.1/{chainId}/quote` - Find best quote to swap on specific chain
- **Swap**: `GET /swap/v6.1/{chainId}/swap` - Generate calldata for swap on specific chain
- **Approve Spender**: `GET /swap/v6.1/{chainId}/approve/spender` - Get router address for specific chain
- **Approve Transaction**: `GET /swap/v6.1/{chainId}/approve/transaction` - Generate approve calldata for specific chain
- **Allowance**: `GET /swap/v6.1/{chainId}/approve/allowance` - Check token allowance on specific chain

### Token API (Multi-Chain + Chain-Specific)
Supports both multi-chain operations and chain-specific queries:
- **Base URL**: `https://api.1inch.dev/`

**Multi-Chain Operations:**
- **Multi-Chain Tokens**: `GET /token/v1.3/multi-chain` - Get tokens across all chains
- **Multi-Chain Token List**: `GET /token/v1.3/multi-chain/token-list` - Token list format across all chains
- **Multi-Chain Search**: `GET /token/v1.3/search` - Search tokens across chains

**Chain-Specific Operations (chainId as path parameter):**
- **Chain Tokens**: `GET /token/v1.3/{chainId}` - Chain-specific tokens
- **Chain Token List**: `GET /token/v1.3/{chainId}/token-list` - Chain token list format
- **Chain Search**: `GET /token/v1.3/{chainId}/search` - Search tokens on specific chain
- **Custom Tokens**: `GET /token/v1.3/{chainId}/custom` - Get multiple token details on specific chain
- **Custom Token**: `GET /token/v1.3/{chainId}/custom/{address}` - Get single token details on specific chain

### Token Details API (Chain-Specific)
ChainId is required as path parameter for all operations:
- **Base URL**: `https://api.1inch.dev/`
- **Native Token Details**: `GET /token-details/v1.0/details/{chainId}` - Native token info for specific chain
- **Token Details**: `GET /token-details/v1.0/details/{chainId}/{contractAddress}` - Token details with pricing for specific chain
- **Charts**: `GET /token-details/v1.0/charts/*/{chainId}/*` - Chart data for tokens on specific chain

### Orderbook API (Chain-Specific)
ChainId is required as path parameter for all operations:
- **Base URL**: `https://api.1inch.dev/`
- **Create Order**: `POST /orderbook/v4.0/{chainId}` - Create limit order on specific chain
- **Get Orders**: `GET /orderbook/v4.0/{chainId}/*` - All order operations require specific chain

### History API (Multi-Chain)
ChainId is optional query parameter - can work across chains:
- **Base URL**: `https://api.1inch.dev/`
- **History Events**: `GET /history/v2.0/history/{address}/events?chainId={chainId}` - Get transaction history (chainId optional)

### Portfolio API v5 (Multi-Chain)
ChainId is optional query parameter - can work across chains:
- **Base URL**: `https://api.1inch.dev/`
- **Service Status**: `GET /portfolio/v5.0/general/status` - Check service availability
- **Address Check**: `GET /portfolio/v5.0/general/address_check` - Check addresses for compliance
- **Supported Chains**: `GET /portfolio/v5.0/general/supported_chains` - Get supported blockchain networks
- **Supported Protocols**: `GET /portfolio/v5.0/general/supported_protocols` - Get supported DeFi protocol groups
- **Current Value**: `GET /portfolio/v5.0/general/current_value` - Get portfolio value breakdown by address, category, protocol, chain
- **Value Chart**: `GET /portfolio/v5.0/general/chart` - Get historical value chart data
- **Report**: `GET /portfolio/v5.0/general/report` - Get CSV report with portfolio details
- **Protocols Snapshot**: `GET /portfolio/v5.0/protocols/snapshot` - Get protocols snapshot with underlying tokens, rewards, fees
- **Protocols Metrics**: `GET /portfolio/v5.0/protocols/metrics` - Get protocols metrics with P&L, ROI, APR calculations
- **Tokens Snapshot**: `GET /portfolio/v5.0/tokens/snapshot` - Get tokens snapshot
- **Tokens Metrics**: `GET /portfolio/v5.0/tokens/metrics` - Get tokens metrics with P&L, ROI calculations

### Balance API v1.2 (Chain-Specific)
ChainId is required as path parameter for all operations:
- **Base URL**: `https://api.1inch.dev/`
- **Get Balances**: `GET /balance/v1.2/{chainId}/balances/{walletAddress}` - Get all token balances for wallet on specific chain
- **Get Custom Balances**: `POST /balance/v1.2/{chainId}/balances/{walletAddress}` - Get balances for specific tokens on specific chain
- **Get Allowances**: `GET /balance/v1.2/{chainId}/allowances/{spender}/{walletAddress}` - Get token allowances by spender on specific chain
- **Get Custom Allowances**: `POST /balance/v1.2/{chainId}/allowances/{spender}/{walletAddress}` - Get allowances for specific tokens on specific chain
- **Get Balances and Allowances**: `GET /balance/v1.2/{chainId}/allowancesAndBalances/{spender}/{walletAddress}` - Get combined data on specific chain
- **Get Custom Balances and Allowances**: `POST /balance/v1.2/{chainId}/allowancesAndBalances/{spender}/{walletAddress}` - Get combined data for specific tokens on specific chain
- **Get Aggregated Data**: `GET /balance/v1.2/{chainId}/aggregatedBalancesAndAllowances/{spender}` - Get aggregated data for multiple wallets on specific chain
- **Get Multi-Wallet Balances**: `POST /balance/v1.2/{chainId}/balances/multiple/walletsAndTokens` - Get balances for multiple wallets and tokens on specific chain

## Common Maven Commands
```bash
mvn clean compile    # Compile the project
mvn test            # Run unit tests
mvn package         # Build JAR
mvn install         # Install to local repository
```

## Implementation Notes
- All monetary amounts are in wei (string format)
- Token addresses are checksummed Ethereum addresses
- API requires authentication via API_KEY header
- Slippage is in basis points (e.g., 1 = 1%)
- Gas prices are in wei

## Swagger Documentation
- API Swagger documentation can be found in ./swagger/ folder in corresponding sub folder.

All API specifications are organized in the `./swagger/` directory with full OpenAPI 3.0 schema definitions:

```
swagger/
├── balance/ethereum.json           # Balance API
├── charts/charts.json              # Charts API  
├── cross-chain/                    # Cross-chain APIs
│   ├── orders/ethereum.json
│   ├── quoter/ethereum.json
│   └── relayer/ethereum.json
├── domains/swagger.json            # Domains API
├── fusion-plus/                    # Fusion+ APIs
│   ├── orders/ethereum.json
│   ├── quoter/ethereum.json
│   └── relayer/ethereum.json
├── fusion/                         # Fusion APIs (multi-chain)
│   ├── orders/[arbitrum|avalanche|binance|ethereum|gnosis|optimism|polygon].json
│   ├── quoter/[arbitrum|avalanche|binance|ethereum|gnosis|optimism|polygon].json
│   └── relayer/[arbitrum|avalanche|binance|ethereum|gnosis|optimism|polygon].json
├── gas-price/ethereum.json         # Gas Price API
├── history/swagger.json            # History API
├── nft/nft.json                    # NFT API
├── orderbook/ethereum.json         # Orderbook API
├── portfolio/portfolio.json        # Portfolio API
├── price/ethereum.json             # Price API
├── spot-price/ethereum.json        # Spot Price API
├── swap/ethereum.json              # Swap API (main implementation)
├── token-details/swagger.json      # Token Details API
├── token/token.json                # Token API
├── traces/traces.json              # Traces API
├── transaction/ethereum.json       # Transaction API
├── tx-gateway/ethereum.json        # Transaction Gateway API
└── web3/web3.json                  # Web3 API
```

**Key API Files**: 
- `./swagger/swap/ethereum.json` - Swap API endpoints
- `./swagger/token/token.json` - Token API endpoints
- `./swagger/token-details/swagger.json` - Token Details API endpoints
- `./swagger/history/swagger.json` - History API endpoints
- `./swagger/portfolio/v5/portfolio.json` - Portfolio API endpoints
- `./swagger/balance/ethereum.json` - Balance API endpoints

[... rest of the existing content remains the same ...]

## Memories

### Development Practices
- Prior to adding new model types into model: always check - is it possible to reuse some of the existing model types.
- Use "git mv" for any file moves. Switch to "mv" only when file eventually was not under git version control yet.

### Memory Updates
- Always update README.md and CLAUDE.md when some impacting changes are happening.