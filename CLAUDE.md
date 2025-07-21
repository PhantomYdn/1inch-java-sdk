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

## 1inch API Endpoints (Ethereum)
- **Base URL**: `https://api.1inch.dev/swap/v6.0/1/`
- **Quote**: `GET /quote` - Find best quote to swap
- **Swap**: `GET /swap` - Generate calldata for swap
- **Approve Spender**: `GET /approve/spender` - Get router address
- **Approve Transaction**: `GET /approve/transaction` - Generate approve calldata
- **Allowance**: `GET /approve/allowance` - Check token allowance
- **Liquidity Sources**: `GET /liquidity-sources` - List available protocols
- **Tokens**: `GET /tokens` - List supported tokens

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

**Main Swap API**: Located at `./swagger/swap/ethereum.json` - this contains the primary API endpoints implemented in the SDK.

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