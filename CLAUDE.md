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
- **Base URL**: `https://api.1inch.dev/swap/v6.0/1/` (Ethereum)
- **Quote**: `GET /quote` - Find best quote to swap
- **Swap**: `GET /swap` - Generate calldata for swap
- **Approve Spender**: `GET /approve/spender` - Get router address
- **Approve Transaction**: `GET /approve/transaction` - Generate approve calldata
- **Allowance**: `GET /approve/allowance` - Check token allowance

### Token API (Multi-Chain)
- **Base URL**: `https://api.1inch.dev/`
- **Multi-Chain Tokens**: `GET /token/v1.2/multi-chain` - Get tokens across all chains
- **Multi-Chain Token List**: `GET /token/v1.2/multi-chain/token-list` - Token list format
- **Chain Tokens**: `GET /token/v1.2/{chainId}` - Chain-specific tokens
- **Chain Token List**: `GET /token/v1.2/{chainId}/token-list` - Chain token list format
- **Multi-Chain Search**: `GET /token/v1.2/search` - Search tokens across chains
- **Chain Search**: `GET /token/v1.2/{chainId}/search` - Search tokens on specific chain
- **Custom Tokens**: `GET /token/v1.2/{chainId}/custom` - Get multiple token details
- **Custom Token**: `GET /token/v1.2/{chainId}/custom/{address}` - Get single token details

### Token Details API (Pricing & Charts)
- **Base URL**: `https://api.1inch.dev/`
- **Native Token Details**: `GET /token-details/v1.0/details/{chain}` - Native token info
- **Token Details**: `GET /token-details/v1.0/details/{chain}/{contractAddress}` - Token details with pricing

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
- **Multiple Retrofit instances**: Separate instances for different API base URLs
- **Swap API**: `https://api.1inch.dev/swap/v6.0/1/` (chain-specific)
- **Token API**: `https://api.1inch.dev/` (multi-chain)
- **Shared authentication**: Same Bearer token for all APIs
- **Connection pooling**: OkHttp connection reuse across all services

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