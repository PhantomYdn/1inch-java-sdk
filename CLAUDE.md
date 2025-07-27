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

## Token API Implementation

### Service Architecture
The Token API is implemented using a dual-service architecture:

#### TokenService Interface
Provides comprehensive token operations:
- **Multi-chain operations**: Get tokens across all supported chains
- **Chain-specific operations**: Get tokens for specific blockchain
- **Search functionality**: Search tokens by name/symbol across chains
- **Custom token info**: Get detailed information for specific token addresses
- **Token details with pricing**: Get market data, charts, and metadata

#### API Service Classes
- **OneInchTokenApiService**: Handles core token operations (lists, search, custom tokens)
- **OneInchTokenDetailsApiService**: Handles token details with pricing/chart data

### Key Features

#### Multi-Chain Support
```java
// Get tokens across all chains
TokenListRequest request = TokenListRequest.builder()
    .provider("1inch")
    .country("US")
    .build();

Map<String, ProviderTokenDto> tokens = client.token().getMultiChainTokens(request);
```

#### Token Search
```java
// Search tokens across multiple chains
TokenSearchRequest searchRequest = TokenSearchRequest.builder()
    .query("1inch")
    .onlyPositiveRating(true)
    .limit(10)
    .build();

List<TokenDto> results = client.token().searchMultiChainTokens(searchRequest);
```

#### BigInteger Precision
All amount-related fields use `BigInteger` for high precision:
- Token amounts (can exceed `Long.MAX_VALUE` for wei amounts)
- Market cap values
- Supply calculations
- Price calculations

#### Model Classes
**Request Models**:
- `TokenListRequest` - For token list operations
- `TokenSearchRequest` - For search operations  
- `CustomTokenRequest` - For custom token lookups
- `TokenDetailsRequest` - For detailed token information

**Response Models**:
- `TokenListResponse` - Standard token list format
- `TokenDto` - Individual token information with rating
- `ProviderTokenDto` - Provider-specific token data
- `TokenDetailsResponse` - Comprehensive token details with pricing
- `AssetsResponse` - Token metadata (website, description, etc.)
- `DetailsResponse` - Market data (market cap, volume, supply)

### Integration Points

#### Client Integration
```java
OneInchClient client = OneInchClient.builder()
    .apiKey("your-api-key")
    .build();

// Access token service
TokenService tokenService = client.token();

// All three programming models supported
Map<String, ProviderTokenDto> syncTokens = tokenService.getTokens(request);
CompletableFuture<Map<String, ProviderTokenDto>> asyncTokens = tokenService.getTokensAsync(request);
Single<Map<String, ProviderTokenDto>> rxTokens = tokenService.getTokensRx(request);
```

#### HTTP Client Architecture
- **Single Retrofit instance**: All APIs use the same base URL and client
- **Base URL**: `https://api.1inch.dev/` (unified for all APIs)
- **Shared authentication**: Same Bearer token for all APIs
- **Connection pooling**: OkHttp connection reuse across all services

**API Routing Strategy:**
- **Chain-Specific APIs**: ChainId in path parameter (`/swap/v6.1/{chainId}/`, `/orderbook/v4.0/{chainId}/`, `/token-details/v1.0/*/{chainId}/`, `/balance/v1.2/{chainId}/`)
- **Multi-Chain APIs**: ChainId as optional query parameter (`/history/v2.0/history/{address}/events?chainId=1`, `/portfolio/v5.0/general/*`)
- **Hybrid APIs**: Token API supports both multi-chain endpoints (`/token/v1.3/multi-chain`) and chain-specific endpoints (`/token/v1.3/{chainId}`)

## History API Implementation

### Service Architecture
The History API provides transaction history for user addresses with comprehensive filtering capabilities.

#### HistoryService Interface
Provides transaction history operations:
- **Address-based history**: Get transaction events for specific addresses
- **Time filtering**: Filter events by timestamp ranges
- **Token filtering**: Filter events by specific token addresses
- **Chain filtering**: Filter events by blockchain network
- **Pagination**: Control result sets with limit parameters

### Key Features

#### Transaction History Retrieval
```java
// Get recent transaction history for an address
HistoryEventsRequest request = HistoryEventsRequest.builder()
    .address("0x111111111117dc0aa78b770fa6a738034120c302")
    .limit(10)
    .chainId(1) // Ethereum
    .build();

HistoryResponseDto response = client.history().getHistoryEvents(request);
```

#### Time-based Filtering
```java
// Get history for specific time range
HistoryEventsRequest request = HistoryEventsRequest.builder()
    .address("0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8")
    .fromTimestampMs("1694754179096")
    .toTimestampMs("1695283931212")
    .limit(50)
    .build();

HistoryResponseDto response = client.history().getHistoryEvents(request);
```

#### Model Classes
**Request Models**:
- `HistoryEventsRequest` - For history event queries with filtering

**Response Models**:
- `HistoryResponseDto` - Main response wrapper with events and cache counter
- `HistoryEventDto` - Individual transaction event
- `TransactionDetailsDto` - Detailed transaction information
- `TransactionDetailsMetaDto` - Additional transaction metadata
- `TokenActionDto` - Token transfer/action details

**Enum Models**:
- `TransactionType` - Transaction types (Transfer, SwapExactInput, Approve, etc.)
- `HistoryEventType` - Event types (Transaction, LimitOrder, FusionSwap)
- `TokenActionDirection` - Token action directions (In, Out, Self, On)
- `EventRating` - Event ratings (Reliable, Scam)
- `TransactionStatus` - Transaction status (Completed, Failed, etc.)

#### BigInteger Precision
All amount-related fields use `BigInteger` for high precision:
- Token amounts in wei
- Fee amounts
- Transaction values
- Gas prices and limits

### Integration Points

#### Client Integration
```java
OneInchClient client = OneInchClient.builder()
    .apiKey("your-api-key")
    .build();

// Access history service
HistoryService historyService = client.history();

// All three programming models supported
HistoryResponseDto syncHistory = historyService.getHistoryEvents(request);
CompletableFuture<HistoryResponseDto> asyncHistory = historyService.getHistoryEventsAsync(request);
Single<HistoryResponseDto> rxHistory = historyService.getHistoryEventsRx(request);
```

#### API Service Classes
- **OneInchHistoryApiService**: Retrofit interface for History API endpoints
- **HistoryServiceImpl**: Service implementation with error handling and logging

## Portfolio API v5 Implementation

### Service Architecture
The Portfolio API v5 provides comprehensive DeFi position tracking and analytics across multiple chains and protocols.

#### PortfolioService Interface
Provides DeFi portfolio operations:
- **Service status**: Check API availability and supported chains/protocols
- **Address validation**: Validate wallet addresses for compliance
- **Current value**: Get portfolio value breakdown by address, category, protocol, and chain
- **Position snapshots**: Get detailed protocol and token position data
- **P&L metrics**: Get profit/loss, ROI, and APR calculations
- **Historical data**: Get value charts and reports over time

### Key Features

#### Service Information and Status
```java
// Check service availability
ApiStatusResponse status = client.portfolio().getServiceStatus();

// Get supported chains
List<SupportedChainResponse> chains = client.portfolio().getSupportedChains();

// Get supported protocols
List<SupportedProtocolGroupResponse> protocols = client.portfolio().getSupportedProtocols();
```

#### Portfolio Value Analysis
```java
// Get current portfolio value breakdown
PortfolioV5OverviewRequest request = PortfolioV5OverviewRequest.builder()
    .addresses(Arrays.asList("0x111111111117dc0aa78b770fa6a738034120c302"))
    .chainId(1) // Ethereum
    .build();

CurrentValueResponse currentValue = client.portfolio().getCurrentValue(request);
```

#### Position Snapshots
```java
// Get protocol positions with underlying tokens
PortfolioV5SnapshotRequest snapshotRequest = PortfolioV5SnapshotRequest.builder()
    .addresses(Arrays.asList("0x111111111117dc0aa78b770fa6a738034120c302"))
    .chainId(1) // Ethereum
    .build();

List<AdapterResult> protocolsSnapshot = client.portfolio().getProtocolsSnapshot(snapshotRequest);
List<AdapterResult> tokensSnapshot = client.portfolio().getTokensSnapshot(snapshotRequest);
```

#### P&L and ROI Metrics
```java
// Get profit/loss and ROI calculations
PortfolioV5MetricsRequest metricsRequest = PortfolioV5MetricsRequest.builder()
    .addresses(Arrays.asList("0x111111111117dc0aa78b770fa6a738034120c302"))
    .chainId(1) // Ethereum
    .build();

List<HistoryMetrics> protocolsMetrics = client.portfolio().getProtocolsMetrics(metricsRequest);
List<HistoryMetrics> tokensMetrics = client.portfolio().getTokensMetrics(metricsRequest);
```

#### Model Classes
**Request Models**:
- `PortfolioV5OverviewRequest` - For current value and overview operations
- `PortfolioV5SnapshotRequest` - For protocol and token snapshot operations
- `PortfolioV5MetricsRequest` - For P&L and ROI metrics operations
- `PortfolioV5ChartRequest` - For historical value chart data
- `AddressValidationRequest` - For address compliance checking

**Response Models**:
- `CurrentValueResponse` - Portfolio value breakdown by address, category, chain
- `AdapterResult` - Protocol/token position details with underlying tokens and rewards
- `HistoryMetrics` - P&L, ROI, APR calculations with holding time analysis
- `ApiStatusResponse` - Service status and availability
- `SupportedChainResponse` - Supported blockchain network information
- `SupportedProtocolGroupResponse` - Supported DeFi protocol information
- `AddressValidationResponse` - Address validation results

#### BigDecimal Precision
Portfolio values and financial calculations use `BigDecimal` for high precision:
- USD values and portfolio totals
- P&L calculations
- ROI and APR percentages
- Token amounts and market values

### Integration Points

#### Client Integration
```java
OneInchClient client = OneInchClient.builder()
    .apiKey("your-api-key")
    .build();

// Access portfolio service
PortfolioService portfolioService = client.portfolio();

// All three programming models supported
CurrentValueResponse syncValue = portfolioService.getCurrentValue(request);
CompletableFuture<CurrentValueResponse> asyncValue = portfolioService.getCurrentValueAsync(request);
Single<CurrentValueResponse> rxValue = portfolioService.getCurrentValueRx(request);
```

#### API Service Classes
- **PortfolioApiClient**: Retrofit interface for Portfolio API v5 endpoints
- **PortfolioServiceImpl**: Service implementation with error handling and logging

### Portfolio v5 Endpoint Structure

The Portfolio API v5 uses a unified response envelope structure with proper error handling:

#### Response Envelope Pattern
```java
// All Portfolio v5 responses are wrapped in ResponseEnvelope
ResponseEnvelope<CurrentValueResponse> envelope = portfolioApiClient.getCurrentValue(...);
CurrentValueResponse result = envelope.getResult(); // Extract actual data
```

#### Comprehensive Coverage
- **General endpoints**: Status, chains, protocols, current value, charts, reports
- **Protocol endpoints**: Snapshots and metrics for DeFi protocol positions
- **Token endpoints**: Snapshots and metrics for individual token positions
- **Address validation**: Compliance checking for wallet addresses

## Balance API v1.2 Implementation

### Service Architecture
The Balance API v1.2 provides token balance and allowance checking capabilities across different blockchain networks.

#### BalanceService Interface
Provides comprehensive balance and allowance operations:
- **Basic balance operations**: Get all token balances for wallet addresses
- **Custom balance operations**: Get balances for specific token lists
- **Allowance operations**: Check token approvals for spender addresses (DEX routers)
- **Combined operations**: Get both balance and allowance data in single calls
- **Multi-wallet operations**: Aggregate data across multiple wallet addresses
- **Chain-specific**: All operations require chainId parameter

### Key Features

#### Basic Balance Checking
```java
// Get all token balances for a wallet
BalanceRequest request = BalanceRequest.builder()
    .chainId(1) // Ethereum
    .walletAddress("0x111111111117dc0aa78b770fa6a738034120c302")
    .build();

Map<String, String> balances = client.balance().getBalances(request);
```

#### Custom Token Balance Queries
```java
// Get balances for specific tokens only
CustomBalanceRequest request = CustomBalanceRequest.builder()
    .chainId(1) // Ethereum
    .walletAddress("0x111111111117dc0aa78b770fa6a738034120c302")
    .tokens(Arrays.asList("0xA0b86a33E6aB6b6ce4e5a5B7db2e8Df6b1D2b9C7"))
    .build();

Map<String, String> customBalances = client.balance().getCustomBalances(request);
```

#### Allowance Checking for DEX Integration
```java
// Check token allowances for 1inch router
AllowanceBalanceRequest request = AllowanceBalanceRequest.builder()
    .chainId(1) // Ethereum
    .spender("0x1111111254eeb25477b68fb85ed929f73a960582") // 1inch v5 router
    .walletAddress("0x111111111117dc0aa78b770fa6a738034120c302")
    .build();

Map<String, String> allowances = client.balance().getAllowances(request);
```

#### Combined Balance and Allowance Data
```java
// Get both balance and allowance in single call
AllowanceBalanceRequest request = AllowanceBalanceRequest.builder()
    .chainId(1) // Ethereum
    .spender("0x1111111254eeb25477b68fb85ed929f73a960582")
    .walletAddress("0x111111111117dc0aa78b770fa6a738034120c302")
    .build();

Map<String, BalanceAndAllowanceItem> combined = client.balance().getAllowancesAndBalances(request);
```

#### Model Classes
**Request Models**:
- `BalanceRequest` - For basic balance queries
- `CustomBalanceRequest` - For custom token balance queries
- `AllowanceBalanceRequest` - For allowance queries
- `CustomAllowanceBalanceRequest` - For custom token allowance queries
- `AggregatedBalanceRequest` - For multi-wallet aggregated queries
- `MultiWalletBalanceRequest` - For multiple wallets and tokens

**Response Models**:
- `BalanceAndAllowanceItem` - Combined balance and allowance data
- `AggregatedBalanceResponse` - Rich aggregated response with token metadata
- Standard `Map<String, String>` responses for simple balance/allowance queries
- `Map<String, Map<String, String>>` for multi-wallet responses

**Internal Request Models**:
- `CustomTokensBalanceRequest` - Maps to swagger CustomTokensRequest schema
- `CustomTokensAndWalletsBalanceRequest` - Maps to swagger CustomTokensAndWalletsRequest schema

#### String Precision for Wei Amounts
All balance and allowance amounts are returned as `String` type for precision:
- Token balances in wei (can exceed `Long.MAX_VALUE`)
- Allowance amounts in wei
- Direct conversion to `BigInteger` for arithmetic operations
- Maintains full precision for large token amounts

### Integration Points

#### Client Integration
```java
OneInchClient client = OneInchClient.builder()
    .apiKey("your-api-key")
    .build();

// Access balance service
BalanceService balanceService = client.balance();

// All three programming models supported
Map<String, String> syncBalances = balanceService.getBalances(request);
CompletableFuture<Map<String, String>> asyncBalances = balanceService.getBalancesAsync(request);
Single<Map<String, String>> rxBalances = balanceService.getBalancesRx(request);
```

#### API Service Classes
- **OneInchBalanceApiService**: Retrofit interface for Balance API v1.2 endpoints
- **BalanceServiceImpl**: Service implementation with error handling and logging

### Balance API Endpoint Structure

The Balance API v1.2 uses chain-specific routing with comprehensive operation coverage:

#### Endpoint Categories
- **Basic Operations**: Simple GET requests for all tokens
- **Custom Operations**: POST requests with token lists in request body
- **Allowance Operations**: Include spender address for DEX approval checking
- **Combined Operations**: Get both balance and allowance data efficiently
- **Multi-wallet Operations**: Portfolio-level balance aggregation

#### Chain-Specific Design
All Balance API endpoints require chainId as path parameter:
- `/balance/v1.2/{chainId}/balances/{walletAddress}` - Chain-specific balance queries
- `/balance/v1.2/{chainId}/allowances/{spender}/{walletAddress}` - Chain-specific allowance queries
- Follows same pattern as Swap and Orderbook APIs for consistency

## Architecture Notes

### Programming Approaches
The SDK provides three equally supported programming patterns:

#### Reactive Programming (RxJava 3)
- **Methods**: End with `Rx` suffix (e.g., `getQuoteRx`)
- **Returns**: `Single<T>` for reactive composition
- **Best for**: Complex async workflows, chaining operations, backpressure handling
- **Error handling**: Reactive operators like `doOnError`, `onErrorResumeNext`

#### Synchronous Programming
- **Methods**: Standard method names (e.g., `getQuote`)
- **Returns**: Direct response objects
- **Best for**: Simple scripts, blocking workflows, traditional programming
- **Error handling**: Throws `OneInchException` for easy try-catch patterns

#### Asynchronous Programming (CompletableFuture)
- **Methods**: End with `Async` suffix (e.g., `getQuoteAsync`)
- **Returns**: `CompletableFuture<T>` for async composition
- **Best for**: Java 8+ async patterns, parallel execution
- **Error handling**: CompletionException wrapping for async error propagation

### HTTP Layer
- **OkHttp**: Modern HTTP client with connection pooling and HTTP/2 support
- **Retrofit**: Type-safe REST client for API integration
- **Jackson**: JSON serialization/deserialization
- **Authentication**: Bearer token authentication via API_KEY header

## Memories

### Development Practices
- Prior to adding new model types into model: always check - is it possible to reuse some of the existing model types.

### Memory Updates
- Always update README.md and CLAUDE.md when some impacting changes are happening.

