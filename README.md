# 1inch Java SDK

A comprehensive Java SDK for the 1inch DEX Aggregation Protocol, providing easy integration with 1inch's swap, token, token-details, orderbook, and history services.

## Features

- ✅ Java 11 compatible
- ✅ Modern reactive programming with RxJava 3
- ✅ Interface-driven design for easy testing and mocking
- ✅ Multiple programming approaches: reactive, synchronous, and asynchronous
- ✅ OkHttp with HTTP/2 support and connection pooling
- ✅ Type-safe REST API integration with Retrofit 2
- ✅ Comprehensive error handling
- ✅ Built-in logging with SLF4J
- ✅ Full Swap API support with chainId path parameters
- ✅ Complete Token API with multi-chain support
- ✅ Token Details API with pricing and chart data
- ✅ Orderbook API for limit order management
- ✅ History API for transaction history tracking
- ✅ Portfolio API for DeFi position tracking and analytics
- ✅ Balance API for token balance and allowance checking
- ✅ Type-safe models with Jackson and BigInteger precision
- ✅ Extensive unit test coverage

## Project Structure

This project is organized as a multi-module Maven project:

- **`oneinch-sdk-core`** - The core SDK functionality with all APIs and models
- **`oneinch-sdk-examples`** - Example applications demonstrating SDK usage

## Installation

Add the core SDK dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.oneinch.sdk</groupId>
    <artifactId>oneinch-sdk-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Building from Source

### Build Everything
```bash
git clone https://github.com/1inch/1inch-java-sdk.git
cd 1inch-java-sdk
mvn clean install
```

### Build Only Core SDK
```bash
mvn clean install -pl oneinch-sdk-core
```

### Build Only Examples  
```bash
mvn clean install -pl oneinch-sdk-examples
```

## High-Precision Arithmetic

The SDK uses `BigInteger` for all amount-related fields to ensure precision when handling cryptocurrency values, which can exceed the range of standard numeric types:

- **Amount fields**: `QuoteRequest.amount`, `SwapRequest.amount`
- **Response amounts**: `QuoteResponse.dstAmount`, `SwapResponse.dstAmount`
- **Gas-related fields**: `gasPrice`, `gasLimit`, `gas`
- **Transaction values**: `TransactionData.value`, `TransactionData.gasPrice`
- **Token allowances**: `AllowanceResponse.allowance`

```java
import java.math.BigInteger;

// Wei amounts (18 decimals for ETH)
BigInteger oneEth = new BigInteger("1000000000000000000");
BigInteger halfEth = new BigInteger("500000000000000000");

// Gas price in wei (20 gwei)
BigInteger gasPrice = new BigInteger("20000000000");

QuoteRequest request = QuoteRequest.builder()
    .amount(oneEth)
    .gasPrice(gasPrice)
    .build();
```

**Why BigInteger?**
- Ethereum amounts are measured in wei (10^18 units per ETH)
- Values can exceed `Long.MAX_VALUE` (9,223,372,036,854,775,807)
- BigInteger prevents overflow and precision loss
- Native JSON serialization support via Jackson

## Programming Approaches

The SDK supports three programming approaches to fit different use cases:

### 🔄 Reactive (RxJava)
- **Best for**: Complex async workflows, chaining operations, backpressure handling
- **Returns**: `Single<T>` for reactive composition
- **Example**: `client.swap().getQuoteRx(request)`

### ⚡ Synchronous 
- **Best for**: Simple scripts, blocking workflows, traditional programming
- **Returns**: Direct response objects
- **Example**: `client.swap().getQuote(request)`

### 🚀 Asynchronous (CompletableFuture)
- **Best for**: Java 8+ async patterns, parallel execution
- **Returns**: `CompletableFuture<T>` for async composition  
- **Example**: `client.swap().getQuoteAsync(request)`

## Quick Start

### Reactive Approach

```java
import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.model.QuoteRequest;
import io.oneinch.sdk.model.QuoteResponse;
import lombok.extern.slf4j.Slf4j;
import java.math.BigInteger;

@Slf4j
public class ReactiveExample {
    public void getQuote() {
        // Initialize the client (using ONEINCH_API_KEY environment variable)
        try (OneInchClient client = OneInchClient.builder()
                .build()) {
            
            // Get a quote for swapping ETH to 1INCH (reactive)
            QuoteRequest quoteRequest = QuoteRequest.builder()
                .chainId(1)  // Ethereum
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")  // ETH
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")  // 1INCH
                .amount(new BigInteger("10000000000000000"))  // 0.01 ETH in wei
                .includeTokensInfo(true)
                .build();
            
            client.swap().getQuoteRx(quoteRequest)
                .doOnSuccess(quote -> {
                    log.info("Expected output: {} {}", quote.getDstAmount(),
                            quote.getDstToken() != null ? quote.getDstToken().getSymbol() : "tokens");
                })
                .doOnError(error -> log.error("Error getting quote", error))
                .subscribe();
        }
    }
}
```

### Synchronous Approach

```java
import lombok.extern.slf4j.Slf4j;
import java.math.BigInteger;

@Slf4j
public class SynchronousExample {
    public void getQuote() {
        try (OneInchClient client = OneInchClient.builder()
                .apiKey("your-api-key-here")  // Or omit this to use ONEINCH_API_KEY env var
                .build()) {
            
            QuoteRequest quoteRequest = QuoteRequest.builder()
                .chainId(1)  // Ethereum
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")  // ETH
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")  // 1INCH
                .amount(new BigInteger("10000000000000000"))  // 0.01 ETH in wei
                .build();
            
            QuoteResponse quote = client.swap().getQuote(quoteRequest);
            log.info("Expected output: {}", quote.getDstAmount());
        } catch (OneInchException e) {
            log.error("Error getting quote: {}", e.getMessage());
        }
    }
}
```

## API Coverage

### Swap API (`client.swap()`) - **Chain-Specific**
All Swap API operations require a specific chainId and operate on that blockchain:
- ✅ `getQuote(chainId, ...)` - Find the best quote to swap on specific chain
- ✅ `getSwap(chainId, ...)` - Generate calldata for swap execution on specific chain
- ✅ `getSpender(chainId)` - Get 1inch Router address for specific chain
- ✅ `getApproveTransaction(chainId, ...)` - Generate token approval calldata for specific chain
- ✅ `getAllowance(chainId, ...)` - Check token allowance on specific chain

### Token API (`client.token()`) - **Multi-Chain + Chain-Specific**
Supports both multi-chain operations and chain-specific queries:

**Multi-Chain Operations:**
- ✅ `getMultiChainTokens()` - Get whitelisted tokens across all chains
- ✅ `getMultiChainTokenList()` - Get token list across all chains
- ✅ `searchMultiChainTokens()` - Search tokens across multiple chains

**Chain-Specific Operations:**
- ✅ `getTokens(chainId, ...)` - Get whitelisted tokens for specific chain
- ✅ `getTokenList(chainId, ...)` - Get chain-specific token list in standard format
- ✅ `searchTokens(chainId, ...)` - Search tokens on specific chain
- ✅ `getCustomTokens(chainId, ...)` - Get token info for multiple custom addresses on specific chain
- ✅ `getCustomToken(chainId, address)` - Get token info for single custom address on specific chain

### Token Details API (`client.tokenDetails()`) - **Chain-Specific**
All Token Details operations require a specific chainId:
- ✅ `getTokenDetails(chainId, address, ...)` - Get token details with pricing data for specific chain
- ✅ `getTokenChart(chainId, address, ...)` - Get token price chart data for specific chain
- ✅ `getTokenPriceChange(chainId, address, ...)` - Get token price changes over time for specific chain
- ✅ `getNativeTokenDetails(chainId, ...)` - Get native token details with pricing for specific chain
- ✅ `getNativeTokenChart(chainId, ...)` - Get native token chart data for specific chain
- ✅ `getNativeTokenChartByRange(chainId, ...)` - Get native token chart for date range on specific chain
- ✅ `getNativeTokenChartByInterval(chainId, ...)` - Get native token chart by interval for specific chain
- ✅ `getNativeTokenPriceChange(chainId, ...)` - Get native token price changes for specific chain
- ✅ `getMultiChainTokenPriceChange(tokens)` - Get multi-chain token price changes (accepts list of chain/token pairs)

### Orderbook API (`client.orderbook()`) - **Chain-Specific**  
All Orderbook operations require a specific chainId:
- ✅ `createLimitOrder(chainId, ...)` - Create a new limit order on specific chain
- ✅ `getLimitOrdersByAddress(chainId, address, ...)` - Get orders for specific address on specific chain
- ✅ `getOrderByOrderHash(chainId, orderHash)` - Get order by hash on specific chain
- ✅ `getAllLimitOrders(chainId, ...)` - Get all limit orders with filters on specific chain
- ✅ `getOrdersCount(chainId, ...)` - Get count of orders by filters on specific chain
- ✅ `getEventsByOrderHash(chainId, orderHash, ...)` - Get events for specific order on specific chain
- ✅ `getAllEvents(chainId, ...)` - Get all order events on specific chain
- ✅ `hasActiveOrdersWithPermit(chainId, ...)` - Check active orders with permit on specific chain
- ✅ `getUniqueActivePairs(chainId, ...)` - Get unique active trading pairs on specific chain

### History API (`client.history()`) - **Multi-Chain**
History operations can work across chains with optional chainId filtering:
- ✅ `getHistoryEvents(address, chainId?, ...)` - Get transaction history for an address with optional chain filtering

### Portfolio API (`client.portfolio()`) - **Multi-Chain**
Portfolio operations provide comprehensive DeFi position tracking and analytics:
- ✅ `getServiceStatus()` - Check Portfolio API service availability
- ✅ `getSupportedChains()` - Get list of supported blockchain networks
- ✅ `getSupportedProtocols()` - Get list of supported DeFi protocols
- ✅ `checkAddresses(addresses)` - Validate wallet addresses for portfolio tracking
- ✅ `getCurrentValue(request)` - Get current portfolio value breakdown by address, category, and chain
- ✅ `getProtocolsSnapshot(request)` - Get detailed protocol positions and underlying tokens
- ✅ `getTokensSnapshot(request)` - Get token positions across all DeFi protocols
- ✅ `getProtocolsMetrics(request)` - Get profit/loss, ROI, and APR metrics for protocol positions
- ✅ `getTokensMetrics(request)` - Get profit/loss, ROI, and APR metrics for token positions

### Balance API (`client.balance()`) - **Chain-Specific**
Balance operations provide token balance and allowance checking across different chains:
- ✅ `getBalances(request)` - Get all token balances for a wallet address on specific chain
- ✅ `getCustomBalances(request)` - Get balances for specific tokens on specific chain
- ✅ `getAllowances(request)` - Get token allowances by spender for wallet on specific chain
- ✅ `getCustomAllowances(request)` - Get allowances for specific tokens by spender on specific chain
- ✅ `getAllowancesAndBalances(request)` - Get combined balance and allowance data on specific chain
- ✅ `getCustomAllowancesAndBalances(request)` - Get combined data for specific tokens on specific chain
- ✅ `getAggregatedBalancesAndAllowances(request)` - Get aggregated data for multiple wallets on specific chain
- ✅ `getBalancesByMultipleWallets(request)` - Get balances for multiple wallets and tokens on specific chain

## Configuration

### Authentication
You need a valid API key from 1inch. Get one at [1inch Developer Portal](https://portal.1inch.dev/).

#### Using Explicit API Key
```java
OneInchClient client = OneInchClient.builder()
    .apiKey("your-api-key")
    .build();
```

#### Using Environment Variable
Set the `ONEINCH_API_KEY` environment variable:
```bash
export ONEINCH_API_KEY="your-api-key-here"
```

Then create the client without explicitly providing the API key:
```java
// Reads API key from ONEINCH_API_KEY environment variable
OneInchClient client = OneInchClient.builder().build();

// Or use the parameterless constructor
OneInchClient client = new OneInchClient();
```

**Priority**: Explicit API key takes precedence over environment variable.

### Custom OkHttp Client
```java
OkHttpClient customOkHttpClient = new OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build();

OneInchClient client = OneInchClient.builder()
    .apiKey("your-api-key")
    .okHttpClient(customOkHttpClient)
    .build();
```

## Examples

### Comprehensive Example Classes

The SDK includes complete example classes demonstrating all APIs:

- **`SwapExample.java`** - Complete swap workflows (reactive, synchronous, parallel)
- **`TokenExample.java`** - Token list, search, and custom token operations
- **`TokenDetailsExample.java`** - Token pricing, charts, and market data
- **`OrderbookExample.java`** - Limit orders, events, and trading pairs
- **`HistoryExample.java`** - Transaction history tracking and analysis
- **`HistoryConsoleExample.java`** - Command-line tool for displaying transaction history
- **`PortfolioExample.java`** - DeFi portfolio tracking, position analysis, and P&L metrics
- **`BalanceExample.java`** - Token balance and allowance checking across chains

#### Running Examples

To run the examples, first set your API key:
```bash
export ONEINCH_API_KEY="your-api-key-here"
```

Then run specific examples:
```bash
# Run the QuickStart example
mvn exec:java -pl oneinch-sdk-examples

# Run the Swap example
mvn exec:java -pl oneinch-sdk-examples -Dexec.mainClass="io.oneinch.sdk.examples.SwapExample"

# Run the Token example
mvn exec:java -pl oneinch-sdk-examples -Dexec.mainClass="io.oneinch.sdk.examples.TokenExample"

# Run the Token Details example
mvn exec:java -pl oneinch-sdk-examples -Dexec.mainClass="io.oneinch.sdk.examples.TokenDetailsExample"

# Run the Orderbook example
mvn exec:java -pl oneinch-sdk-examples -Dexec.mainClass="io.oneinch.sdk.examples.OrderbookExample"

# Run the History example
mvn exec:java -pl oneinch-sdk-examples -Dexec.mainClass="io.oneinch.sdk.examples.HistoryExample"

# Run the History Console example (requires address argument)
mvn exec:java -pl oneinch-sdk-examples -Dexec.mainClass="io.oneinch.sdk.examples.HistoryConsoleExample" -Dexec.args="0x111111111117dc0aa78b770fa6a738034120c302"

# Run the Portfolio example
mvn exec:java -pl oneinch-sdk-examples -Dexec.mainClass="io.oneinch.sdk.examples.PortfolioExample"

# Run the Balance example
mvn exec:java -pl oneinch-sdk-examples -Dexec.mainClass="io.oneinch.sdk.examples.BalanceExample"
```

### Reactive Swap Flow
```java
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.math.BigInteger;

try (OneInchClient client = OneInchClient.builder()
        .build()) {  // Uses ONEINCH_API_KEY environment variable
    
    BigInteger swapAmount = new BigInteger("10000000000000000"); // 0.01 ETH in wei
    String walletAddress = "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8";
    
    // Reactive chaining with proper error handling
    client.swap().getSpenderRx(1)  // Ethereum
        .doOnSuccess(spender -> log.info("Spender: {}", spender.getAddress()))
        .flatMap(spender -> {
            // Check allowance
            AllowanceRequest allowanceRequest = AllowanceRequest.builder()
                .chainId(1)  // Ethereum
                .tokenAddress("0x111111111117dc0aa78b770fa6a738034120c302")
                .walletAddress(walletAddress)
                .build();
            return client.swap().getAllowanceRx(allowanceRequest);
        })
        .flatMap(allowance -> {
            // Get quote
            QuoteRequest quoteRequest = QuoteRequest.builder()
                .chainId(1)  // Ethereum
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")  // ETH
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")  // 1INCH
                .amount(swapAmount)
                .includeTokensInfo(true)
                .includeGas(true)
                .build();
            return client.swap().getQuoteRx(quoteRequest);
        })
        .flatMap(quote -> {
            // Get swap transaction data
            SwapRequest swapRequest = SwapRequest.builder()
                .chainId(1)  // Ethereum
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount(swapAmount)
                .from(walletAddress)
                .origin(walletAddress)
                .slippage(1.0)
                .build();
            return client.swap().getSwapRx(swapRequest);
        })
        .subscribe(
            swap -> {
                log.info("Swap ready!");
                log.info("To: {}", swap.getTx().getTo());
                log.info("Value: {}", swap.getTx().getValue());
                log.info("Expected output: {}", swap.getDstAmount());
            },
            error -> log.error("Swap flow failed", error)
        );
}
```

### Parallel Reactive Operations
```java
import io.reactivex.rxjava3.core.Single;
import java.util.concurrent.TimeUnit;
import java.math.BigInteger;

// Run multiple operations in parallel
Single<SpenderResponse> spenderSingle = client.swap().getSpenderRx(1)  // Ethereum
    .subscribeOn(Schedulers.io());

QuoteRequest quoteRequest = QuoteRequest.builder()
    .chainId(1)  // Ethereum
    .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")  // ETH
    .dst("0x111111111117dc0aa78b770fa6a738034120c302")  // 1INCH
    .amount(new BigInteger("10000000000000000"))
    .build();

Single<QuoteResponse> quoteSingle = client.swap().getQuoteRx(quoteRequest)
    .subscribeOn(Schedulers.io());

AllowanceRequest allowanceRequest = AllowanceRequest.builder()
    .chainId(1)  // Ethereum
    .tokenAddress("0x111111111117dc0aa78b770fa6a738034120c302")
    .walletAddress("0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8")
    .build();

Single<AllowanceResponse> allowanceSingle = client.swap().getAllowanceRx(allowanceRequest)
    .subscribeOn(Schedulers.io());

// Combine all results
Single.zip(spenderSingle, quoteSingle, allowanceSingle,
    (spender, quote, allowance) -> {
        log.info("All operations completed!");
        return "Success";
    })
    .timeout(10, TimeUnit.SECONDS)
    .blockingGet();
```

### Synchronous Swap Flow
```java
try (OneInchClient client = OneInchClient.builder()
        .build()) {  // Uses ONEINCH_API_KEY environment variable
    
    // 1. Check allowance
    AllowanceRequest allowanceRequest = AllowanceRequest.builder()
        .chainId(1)  // Ethereum
        .tokenAddress("0x111111111117dc0aa78b770fa6a738034120c302")
        .walletAddress("0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8")
        .build();
    
    AllowanceResponse allowance = client.swap().getAllowance(allowanceRequest);
    
    // 2. Get quote
    QuoteRequest quoteRequest = QuoteRequest.builder()
        .chainId(1)  // Ethereum
        .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
        .dst("0x111111111117dc0aa78b770fa6a738034120c302")
        .amount(new BigInteger("10000000000000000"))
        .build();
    
    QuoteResponse quote = client.swap().getQuote(quoteRequest);
    
    // 3. Execute swap (async with CompletableFuture)
    SwapRequest swapRequest = SwapRequest.builder()
        .chainId(1)  // Ethereum
        .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
        .dst("0x111111111117dc0aa78b770fa6a738034120c302")
        .amount(new BigInteger("10000000000000000"))
        .from("0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8")
        .origin("0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8")
        .slippage(1.0)
        .build();
    
    CompletableFuture<SwapResponse> swapFuture = client.swap().getSwapAsync(swapRequest);
    SwapResponse swap = swapFuture.get(10, TimeUnit.SECONDS);
    // Execute swap transaction using swap.getTx()...
}
```

### Reactive Error Handling
```java
client.swap().getQuoteRx(invalidRequest)
    .doOnSuccess(quote -> log.info("Quote: {}", quote.getDstAmount()))
    .doOnError(error -> {
        if (error instanceof OneInchApiException) {
            OneInchApiException apiError = (OneInchApiException) error;
            log.error("API Error: {} (Status: {})", apiError.getError(), apiError.getStatusCode());
        }
    })
    .onErrorReturn(error -> {
        // Fallback value
        QuoteResponse fallback = new QuoteResponse();
        fallback.setDstAmount(BigInteger.ZERO);
        return fallback;
    })
    .subscribe(
        quote -> log.info("Final result: {}", quote.getDstAmount()),
        error -> log.error("This shouldn't happen with fallback", error)
    );
```

### Token API Examples

#### Get Multi-Chain Token List
```java
import io.oneinch.sdk.model.*;
import java.math.BigInteger;

try (OneInchClient client = OneInchClient.builder().build()) {
    
    // Get whitelisted tokens across all chains
    TokenListRequest request = TokenListRequest.builder()
            .provider("1inch")
            .build();
    
    // Synchronous
    List<ProviderTokenDto> tokens = client.token().getMultiChainTokens(request);
    log.info("Found {} tokens across all chains", tokens.size());
    
    // Reactive with RxJava
    client.token().getMultiChainTokensRx(request)
            .doOnSuccess(result -> {
                result.stream()
                        .filter(token -> "1INCH".equals(token.getSymbol()))
                        .forEach(token -> log.info("Found 1INCH on chain {}: {}", 
                                token.getChainId(), token.getAddress()));
            })
            .subscribe();
}
```

#### Search for Tokens
```java
// Search for tokens across multiple chains
TokenSearchRequest searchRequest = TokenSearchRequest.builder()
        .query("1inch")
        .onlyPositiveRating(true)
        .limit(10)
        .build();

List<TokenDto> searchResults = client.token().searchMultiChainTokens(searchRequest);
searchResults.forEach(token -> 
    log.info("Found: {} ({}) on chain {} - Rating: {}", 
            token.getName(), token.getSymbol(), token.getChainId(), token.getRating()));

// Search on specific chain
TokenSearchRequest ethSearchRequest = TokenSearchRequest.builder()
        .chainId(1) // Ethereum
        .query("USDC")
        .onlyPositiveRating(true)
        .build();

CompletableFuture<List<TokenDto>> ethResults = client.token().searchTokensAsync(ethSearchRequest);
ethResults.thenAccept(results -> 
    log.info("Found {} USDC tokens on Ethereum", results.size()));
```

#### Get Token List for Specific Chain
```java
// Get Ethereum token list in standard format
TokenListRequest ethRequest = TokenListRequest.builder()
        .chainId(1) // Ethereum
        .provider("1inch")
        .build();

TokenListResponse tokenList = client.token().getTokenList(ethRequest);
log.info("Token list: {} (version {}.{}.{})", 
        tokenList.getName(),
        tokenList.getVersion().getMajor(),
        tokenList.getVersion().getMinor(), 
        tokenList.getVersion().getPatch());

// Find specific tokens
tokenList.getTokens().stream()
        .filter(token -> "USDC".equals(token.getSymbol()))
        .findFirst()
        .ifPresent(usdc -> log.info("USDC: {} decimals at {}", 
                usdc.getDecimals(), usdc.getAddress()));
```

#### Get Custom Token Information
```java
// Get single token info
Integer chainId = 1; // Ethereum
String tokenAddress = "0x111111111117dc0aa78b770fa6a738034120c302"; // 1INCH

TokenDto tokenInfo = client.token().getCustomToken(chainId, tokenAddress);
log.info("Token: {} ({}) - {} decimals", 
        tokenInfo.getName(), tokenInfo.getSymbol(), tokenInfo.getDecimals());

// Get multiple tokens
CustomTokenRequest multiRequest = CustomTokenRequest.builder()
        .chainId(1)
        .addresses(List.of(
                "0x111111111117dc0aa78b770fa6a738034120c302", // 1INCH
                "0xA0b86a33E6aB6b6ce4e5a5B7db2e8Df6b1D2b9C7"  // USDC
        ))
        .build();

Map<String, TokenInfo> tokenInfos = client.token().getCustomTokens(multiRequest);
tokenInfos.forEach((address, info) -> 
    log.info("Token at {}: {} ({})", address, info.getName(), info.getSymbol()));
```

#### Get Token Details with Pricing
```java
// Get native token (ETH) details
TokenDetailsRequest nativeRequest = TokenDetailsRequest.builder()
        .chainId(1) // Ethereum
        .provider("coinmarketcap")
        .build();

TokenDetailsResponse ethDetails = client.tokenDetails().getNativeTokenDetails(nativeRequest);
log.info("ETH Details: Market Cap: ${}, 24h Volume: ${}", 
        ethDetails.getDetails().getMarketCap(),
        ethDetails.getDetails().getVol24());

// Get token details with pricing
TokenDetailsRequest tokenDetailsRequest = TokenDetailsRequest.builder()
        .chainId(1)
        .contractAddress("0x111111111117dc0aa78b770fa6a738034120c302") // 1INCH
        .provider("coingecko")
        .build();

// Reactive approach
client.tokenDetails().getTokenDetailsRx(tokenDetailsRequest)
        .doOnSuccess(details -> {
            log.info("1INCH Token Details:");
            log.info("  Website: {}", details.getAssets().getWebsite());
            log.info("  Description: {}", details.getAssets().getShortDescription());
            log.info("  Market Cap: ${}", details.getDetails().getMarketCap());
            log.info("  Total Supply: {}", details.getDetails().getTotalSupply());
        })
        .subscribe();
```

#### Token Operations with Different Programming Models
```java
// Synchronous - Simple and blocking
TokenListRequest request = TokenListRequest.builder()
        .chainId(1)
        .provider("1inch")
        .build();

try {
    Map<String, ProviderTokenDto> tokens = client.token().getTokens(request);
    // Process tokens...
} catch (OneInchException e) {
    log.error("Failed to get tokens: {}", e.getMessage());
}

// Asynchronous with CompletableFuture
CompletableFuture<Map<String, ProviderTokenDto>> tokensFuture = 
    client.token().getTokensAsync(request);

tokensFuture
    .thenAccept(tokens -> {
        // Process tokens...
        log.info("Received {} tokens", tokens.size());
    })
    .exceptionally(throwable -> {
        log.error("Token request failed", throwable);
        return null;
    });

// Reactive with RxJava - Best for complex chains
client.token().getTokensRx(request)
    .flatMap(tokens -> {
        // Find 1INCH token
        return Single.fromCallable(() -> 
            tokens.values().stream()
                .filter(t -> "1INCH".equals(t.getSymbol()))
                .findFirst()
                .orElse(null)
        );
    })
    .filter(token -> token != null)
    .flatMap(oneInchToken -> {
        // Get detailed information
        TokenDetailsRequest detailsRequest = TokenDetailsRequest.builder()
                .chainId(oneInchToken.getChainId())
                .contractAddress(oneInchToken.getAddress())
                .build();
        return client.tokenDetails().getTokenDetailsRx(detailsRequest);
    })
    .subscribe(
        details -> log.info("1INCH details: {}", details.getAssets().getName()),
        error -> log.error("Chain failed", error)
    );
```

### History Console Tool

The SDK includes a convenient command-line tool for exploring transaction history:

#### Usage
```bash
# Basic usage - get 10 recent transactions on Ethereum
mvn exec:java -pl oneinch-sdk-examples \
  -Dexec.mainClass="io.oneinch.sdk.examples.HistoryConsoleExample" \
  -Dexec.args="0x111111111117dc0aa78b770fa6a738034120c302"

# Get 20 transactions on Polygon (chain ID 137)
mvn exec:java -pl oneinch-sdk-examples \
  -Dexec.mainClass="io.oneinch.sdk.examples.HistoryConsoleExample" \
  -Dexec.args="0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8 137 20"

# Get 50 transactions on BSC (chain ID 56)
mvn exec:java -pl oneinch-sdk-examples \
  -Dexec.mainClass="io.oneinch.sdk.examples.HistoryConsoleExample" \
  -Dexec.args="0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8 56 50"
```

#### Arguments
- **address** (required) - Ethereum address to query
- **chainId** (optional) - Blockchain network ID (default: 1 for Ethereum)
  - `1` = Ethereum, `137` = Polygon, `56` = BSC, `42161` = Arbitrum
- **limit** (optional) - Number of events to fetch (default: 10, max: 10000)

#### Sample Output
```
═══════════════════════════════════════════════════════════════════════════════
📜 TRANSACTION HISTORY
═══════════════════════════════════════════════════════════════════════════════
Address: 0x111111111117dc0aa78b770fa6a738034120c302
Chain:   Ethereum (1)
Events:  3 transactions found
═══════════════════════════════════════════════════════════════════════════════

📋 Event #1 [2023-09-21 15:42:31 UTC]
   ID: 7279741913753607 | Type: Transaction | Rating: ✅ Reliable
   Transaction: 0xdd87f...bfc
   Type: Swap Exact Input | Status: ✅ Completed
   Chain: Ethereum (1) | Block: 18,182,982
   Fee: 0.001234 ETH

   Token Actions:
     ← 1.0 ETH (0x266e...db7 → 0x03f7...329)
     → 1,337.89 1INCH (0x03f7...329 → 0x266e...db7)

────────────────────────────────────────────────────────────────────────────────

📋 Event #2 [2023-09-20 09:15:22 UTC]
   ID: 7279741913753606 | Type: Transaction | Rating: ✅ Reliable
   Transaction: 0xabc12...def
   Type: Transfer | Status: ✅ Completed
   Chain: Ethereum (1) | Block: 18,181,445
   Fee: 0.000456 ETH

   Token Actions:
     → 100.0 TOKEN (0x123a...789 → 0x456b...012)

═══════════════════════════════════════════════════════════════════════════════
✅ Successfully displayed 3 transaction events
═══════════════════════════════════════════════════════════════════════════════
```

#### Features
- **Human-readable formatting** - Clean, easy-to-read transaction display
- **Multi-chain support** - Works with Ethereum, Polygon, BSC, Arbitrum, and more
- **Token action details** - Shows token transfers with amounts and directions
- **Error handling** - Clear error messages for common issues
- **Address validation** - Validates Ethereum address format
- **Flexible parameters** - Configurable chain ID and result limits

### History API Examples

#### Get Transaction History for Address
```java
import io.oneinch.sdk.model.*;
import java.util.List;

try (OneInchClient client = OneInchClient.builder().build()) {
    
    // Get recent transaction history for an address
    String walletAddress = "0x111111111117dc0aa78b770fa6a738034120c302"; // 1INCH token contract
    
    HistoryEventsRequest request = HistoryEventsRequest.builder()
            .address(walletAddress)
            .limit(10)
            .chainId(1) // Ethereum
            .build();
    
    // Synchronous
    HistoryResponseDto response = client.history().getHistoryEvents(request);
    log.info("Found {} history events", response.getItems().size());
    
    // Process each event
    for (HistoryEventDto event : response.getItems()) {
        log.info("Event ID: {}, Type: {}, Rating: {}", 
                event.getId(), event.getType(), event.getRating());
        
        if (event.getDetails() != null) {
            TransactionDetailsDto details = event.getDetails();
            log.info("  Transaction: {} on chain {}", 
                    details.getTxHash(), details.getChainId());
            log.info("  Block: {}, Status: {}, Type: {}", 
                    details.getBlockNumber(), details.getStatus(), details.getType());
            
            // Analyze token actions
            if (details.getTokenActions() != null) {
                for (TokenActionDto tokenAction : details.getTokenActions()) {
                    log.info("    Token Action: {} {} tokens {} -> {}", 
                            tokenAction.getDirection(),
                            tokenAction.getAmount(),
                            tokenAction.getFromAddress(),
                            tokenAction.getToAddress());
                }
            }
        }
    }
}
```

#### History with Time and Token Filters
```java
// Get history for specific time range and token
HistoryEventsRequest filteredRequest = HistoryEventsRequest.builder()
        .address("0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8")
        .limit(20)
        .chainId(1) // Ethereum
        .tokenAddress("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee") // ETH transactions only
        .fromTimestampMs("1694754179096") // From timestamp in milliseconds
        .toTimestampMs("1695283931212")   // To timestamp in milliseconds
        .build();

// Reactive approach with error handling
client.history().getHistoryEventsRx(filteredRequest)
        .doOnSuccess(response -> {
            log.info("Found {} filtered events in time range", response.getItems().size());
            
            // Group by transaction type
            Map<TransactionType, Long> typeCount = response.getItems().stream()
                .filter(event -> event.getDetails() != null)
                .collect(Collectors.groupingBy(
                    event -> event.getDetails().getType(),
                    Collectors.counting()
                ));
                
            typeCount.forEach((type, count) -> 
                log.info("  {}: {} transactions", type, count));
        })
        .doOnError(error -> log.error("Failed to get filtered history", error))
        .subscribe();
```

#### Reactive History Analysis Chain
```java
// Complex reactive chain for analyzing transaction patterns
String targetAddress = "0x111111111117dc0aa78b770fa6a738034120c302";

client.history().getHistoryEventsRx(
    HistoryEventsRequest.builder()
        .address(targetAddress)
        .limit(100)
        .chainId(1)
        .build()
)
.map(response -> response.getItems())
.flatMapObservable(events -> Observable.fromIterable(events))
.filter(event -> event.getDetails() != null)
.filter(event -> event.getDetails().getType() == TransactionType.SWAP_EXACT_INPUT)
.groupBy(event -> event.getDetails().getFromAddress()) // Group by sender
.flatMapSingle(group -> 
    group.reduce(new HashMap<String, BigInteger>(), (acc, event) -> {
        // Accumulate swap volumes by sender
        if (event.getDetails().getTokenActions() != null) {
            for (TokenActionDto action : event.getDetails().getTokenActions()) {
                if (action.getDirection() == TokenActionDirection.OUT) {
                    acc.merge(group.getKey(), action.getAmount(), BigInteger::add);
                }
            }
        }
        return acc;
    })
    .map(volumeMap -> Map.entry(group.getKey(), volumeMap.values().stream()
        .reduce(BigInteger.ZERO, BigInteger::add)))
)
.toList()
.subscribe(
    senderVolumes -> {
        log.info("Top swap senders by volume:");
        senderVolumes.stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(5)
            .forEach(entry -> log.info("  {}: {} wei total volume", 
                entry.getKey(), entry.getValue()));
    },
    error -> log.error("Analysis failed", error)
);
```

#### History with All Programming Models
```java
HistoryEventsRequest request = HistoryEventsRequest.builder()
        .address("0x111111111117dc0aa78b770fa6a738034120c302")
        .limit(5)
        .chainId(1)
        .build();

// Synchronous - Simple and blocking
try {
    HistoryResponseDto response = client.history().getHistoryEvents(request);
    log.info("Sync: {} events retrieved", response.getItems().size());
} catch (OneInchException e) {
    log.error("Sync failed: {}", e.getMessage());
}

// Asynchronous with CompletableFuture
CompletableFuture<HistoryResponseDto> historyFuture = 
    client.history().getHistoryEventsAsync(request);

historyFuture
    .thenAccept(response -> 
        log.info("Async: {} events retrieved", response.getItems().size()))
    .exceptionally(throwable -> {
        log.error("Async failed", throwable);
        return null;
    });

// Reactive with RxJava - Best for complex workflows
client.history().getHistoryEventsRx(request)
    .flatMapObservable(response -> Observable.fromIterable(response.getItems()))
    .filter(event -> event.getRating() == EventRating.RELIABLE)
    .filter(event -> event.getDetails() != null)
    .filter(event -> event.getDetails().getType() == TransactionType.TRANSFER)
    .take(3) // Limit to first 3 reliable transfers
    .toList()
    .subscribe(
        reliableTransfers -> log.info("Reactive: Found {} reliable transfers", 
            reliableTransfers.size()),
        error -> log.error("Reactive failed", error)
    );
```

### Portfolio API Examples

The Portfolio API v5 provides comprehensive DeFi position tracking and analytics across multiple chains and protocols.

#### Service Information
```java
try (OneInchClient client = OneInchClient.builder().build()) {
    
    // Check service status
    ApiStatusResponse status = client.portfolio().getServiceStatus();
    log.info("Portfolio API available: {}", status.getIsAvailable());
    
    // Get supported chains
    List<SupportedChainResponse> chains = client.portfolio().getSupportedChains();
    log.info("Supported chains: {}", chains.size());
    chains.stream()
        .limit(3)
        .forEach(chain -> log.info("  Chain: {} ({})", 
            chain.getChainName(), chain.getChainId()));
    
    // Get supported protocols
    List<SupportedProtocolGroupResponse> protocols = client.portfolio().getSupportedProtocols();
    log.info("Supported protocols: {}", protocols.size());
    protocols.stream()
        .limit(3)
        .forEach(protocol -> log.info("  Protocol: {} on chain {}", 
            protocol.getProtocolGroupName(), protocol.getChainId()));
}
```

#### Current Portfolio Value
```java
// Get portfolio value breakdown
PortfolioV5OverviewRequest request = PortfolioV5OverviewRequest.builder()
        .addresses(Arrays.asList("0x111111111117dc0aa78b770fa6a738034120c302"))
        .chainId(1) // Ethereum
        .build();

CurrentValueResponse currentValue = client.portfolio().getCurrentValue(request);

log.info("Total portfolio value: ${}", currentValue.getTotal());

// Value by address
currentValue.getByAddress().forEach(addr -> 
    log.info("  Address {}: ${}", addr.getAddress(), addr.getValueUsd()));

// Value by category (DeFi protocols, tokens, etc.)
currentValue.getByCategory().forEach(cat -> 
    log.info("  Category {}: ${}", cat.getCategoryName(), cat.getValueUsd()));

// Value by blockchain
currentValue.getByChain().forEach(chain -> 
    log.info("  Chain {}: ${}", chain.getChainName(), chain.getValueUsd()));
```

#### Protocol Positions Snapshot
```java
// Get detailed protocol positions
PortfolioV5SnapshotRequest snapshotRequest = PortfolioV5SnapshotRequest.builder()
        .addresses(Arrays.asList("0x111111111117dc0aa78b770fa6a738034120c302"))
        .chainId(1) // Ethereum
        .build();

List<AdapterResult> protocolsSnapshot = client.portfolio().getProtocolsSnapshot(snapshotRequest);

log.info("Found {} protocol positions", protocolsSnapshot.size());

protocolsSnapshot.stream()
    .limit(3)
    .forEach(position -> {
        log.info("  Protocol: {} - Value: ${}", 
            position.getProtocolGroupName(), position.getValueUsd());
        log.info("    Contract: {} ({})", 
            position.getContractName(), position.getContractAddress());
        log.info("    Underlying tokens: {}", position.getUnderlyingTokens().size());
        log.info("    Reward tokens: {}", position.getRewardTokens().size());
    });
```

#### Token Positions Analysis
```java
// Get token positions across all protocols
List<AdapterResult> tokensSnapshot = client.portfolio().getTokensSnapshot(snapshotRequest);

log.info("Found {} token positions", tokensSnapshot.size());

tokensSnapshot.stream()
    .limit(5)
    .forEach(token -> {
        log.info("  Token: {} ({}) - Value: ${}", 
            token.getContractName(), token.getContractSymbol(), token.getValueUsd());
        log.info("    Address: {}", token.getContractAddress());
        log.info("    Locked: {}", token.getLocked());
    });
```

#### P&L and ROI Metrics
```java
// Get profit/loss and ROI metrics for protocols
PortfolioV5MetricsRequest metricsRequest = PortfolioV5MetricsRequest.builder()
        .addresses(Arrays.asList("0x111111111117dc0aa78b770fa6a738034120c302"))
        .chainId(1) // Ethereum
        .build();

List<HistoryMetrics> protocolsMetrics = client.portfolio().getProtocolsMetrics(metricsRequest);

log.info("Found {} protocol metrics", protocolsMetrics.size());

protocolsMetrics.stream()
    .limit(3)
    .forEach(metric -> {
        log.info("  Position: {}", metric.getIndex());
        log.info("    Profit (USD): {}", metric.getProfitAbsUsd());
        log.info("    ROI: {}%", metric.getRoi() != null ? 
            String.format("%.2f", metric.getRoi() * 100) : "N/A");
        log.info("    Weighted APR: {}%", metric.getWeightedApr() != null ? 
            String.format("%.2f", metric.getWeightedApr() * 100) : "N/A");
        log.info("    Holding time: {} days", metric.getHoldingTimeDays());
        log.info("    Rewards (USD): {}", metric.getRewardsUsd());
    });
```

#### Reactive Portfolio Operations
```java
// Reactive portfolio analysis chain
PortfolioV5OverviewRequest request = PortfolioV5OverviewRequest.builder()
        .addresses(Arrays.asList("0x111111111117dc0aa78b770fa6a738034120c302"))
        .chainId(1)
        .build();

client.portfolio().getCurrentValueRx(request)
    .flatMap(currentValue -> {
        log.info("Total value: ${}", currentValue.getTotal());
        
        // Chain to get protocol details
        PortfolioV5SnapshotRequest snapshotRequest = PortfolioV5SnapshotRequest.builder()
            .addresses(request.getAddresses())
            .chainId(request.getChainId())
            .build();
        
        return client.portfolio().getProtocolsSnapshotRx(snapshotRequest);
    })
    .flatMap(protocols -> {
        log.info("Protocol positions: {}", protocols.size());
        
        // Chain to get metrics
        PortfolioV5MetricsRequest metricsRequest = PortfolioV5MetricsRequest.builder()
            .addresses(request.getAddresses())
            .chainId(request.getChainId())
            .build();
        
        return client.portfolio().getProtocolsMetricsRx(metricsRequest);
    })
    .subscribe(
        metrics -> {
            log.info("Portfolio analysis complete!");
            log.info("Found {} protocol metrics", metrics.size());
            
            // Calculate total profit
            BigDecimal totalProfit = metrics.stream()
                .filter(m -> m.getProfitAbsUsd() != null)
                .map(HistoryMetrics::getProfitAbsUsd)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            log.info("Total profit: ${}", totalProfit);
        },
        error -> log.error("Portfolio analysis failed", error)
    );
```

#### Address Validation
```java
// Validate addresses before portfolio operations
AddressValidationRequest validationRequest = AddressValidationRequest.builder()
        .addresses(Arrays.asList(
            "0x111111111117dc0aa78b770fa6a738034120c302",
            "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8"
        ))
        .build();

AddressValidationResponse validation = client.portfolio().checkAddresses(validationRequest);

validation.getResult().forEach((address, isValid) -> 
    log.info("Address {} is {}", address, isValid ? "valid" : "invalid"));
```

### Balance API Examples

The Balance API provides token balance and allowance checking capabilities across different blockchain networks.

#### Basic Wallet Balance Checking
```java
import java.math.BigInteger;

try (OneInchClient client = OneInchClient.builder().build()) {
    
    // Get all token balances for a wallet
    BalanceRequest request = BalanceRequest.builder()
            .chainId(1) // Ethereum
            .walletAddress("0x111111111117dc0aa78b770fa6a738034120c302")
            .build();
    
    Map<String, BigInteger> balances = client.balance().getBalances(request);
    
    log.info("Found {} token balances", balances.size());
    balances.entrySet().stream()
            .filter(entry -> !entry.getValue().equals(BigInteger.ZERO))
            .limit(5)
            .forEach(entry -> log.info("  Token {}: {} wei", entry.getKey(), entry.getValue()));
}
```

#### Custom Token Balance Queries
```java
// Check balances for specific tokens only
CustomBalanceRequest request = CustomBalanceRequest.builder()
        .chainId(1) // Ethereum
        .walletAddress("0x111111111117dc0aa78b770fa6a738034120c302")
        .tokens(Arrays.asList(
            "0xA0b86a33E6aB6b6ce4e5a5B7db2e8Df6b1D2b9C7", // USDC
            "0xdAC17F958D2ee523a2206206994597C13D831ec7"  // USDT
        ))
        .build();

Map<String, BigInteger> customBalances = client.balance().getCustomBalances(request);

customBalances.forEach((token, balance) -> 
    log.info("Token {}: {} wei", token, balance));
```

#### Allowance Checking for DEX Interactions
```java
// Check token allowances for a DEX router
AllowanceBalanceRequest request = AllowanceBalanceRequest.builder()
        .chainId(1) // Ethereum
        .spender("0x1111111254eeb25477b68fb85ed929f73a960582") // 1inch v5 router
        .walletAddress("0x111111111117dc0aa78b770fa6a738034120c302")
        .build();

Map<String, BigInteger> allowances = client.balance().getAllowances(request);

log.info("Token allowances for 1inch router:");
allowances.entrySet().stream()
        .filter(entry -> !entry.getValue().equals(BigInteger.ZERO))
        .forEach(entry -> log.info("  Token {}: {} wei allowance", entry.getKey(), entry.getValue()));
```

#### Combined Balance and Allowance Data
```java
// Get both balance and allowance in a single call
AllowanceBalanceRequest request = AllowanceBalanceRequest.builder()
        .chainId(1) // Ethereum
        .spender("0x1111111254eeb25477b68fb85ed929f73a960582")
        .walletAddress("0x111111111117dc0aa78b770fa6a738034120c302")
        .build();

Map<String, BalanceAndAllowanceItem> combined = client.balance().getAllowancesAndBalances(request);

combined.entrySet().stream()
        .limit(5)
        .forEach(entry -> {
            BalanceAndAllowanceItem item = entry.getValue();
            log.info("Token {}: balance={} wei, allowance={} wei", 
                entry.getKey(), item.getBalance(), item.getAllowance());
        });
```

#### Multi-Wallet Balance Aggregation
```java
// Check balances across multiple wallets for portfolio management
MultiWalletBalanceRequest request = MultiWalletBalanceRequest.builder()
        .chainId(1) // Ethereum
        .wallets(Arrays.asList(
            "0x111111111117dc0aa78b770fa6a738034120c302",
            "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8"
        ))
        .tokens(Arrays.asList(
            "0xA0b86a33E6aB6b6ce4e5a5B7db2e8Df6b1D2b9C7", // USDC
            "0xdAC17F958D2ee523a2206206994597C13D831ec7"  // USDT
        ))
        .build();

Map<String, Map<String, BigInteger>> multiWalletBalances = client.balance().getBalancesByMultipleWallets(request);

multiWalletBalances.forEach((wallet, tokenBalances) -> {
    log.info("Wallet: {}", wallet);
    tokenBalances.forEach((token, balance) -> 
        log.info("  Token {}: {} wei", token, balance));
});
```

#### Reactive Balance Operations
```java
// Reactive balance checking with RxJava
BalanceRequest request = BalanceRequest.builder()
        .chainId(1) // Ethereum
        .walletAddress("0x111111111117dc0aa78b770fa6a738034120c302")
        .build();

client.balance().getBalancesRx(request)
    .flatMap(balances -> {
        log.info("Found {} token balances", balances.size());
        
        // Chain to get allowances
        AllowanceBalanceRequest allowanceRequest = AllowanceBalanceRequest.builder()
            .chainId(request.getChainId())
            .spender("0x1111111254eeb25477b68fb85ed929f73a960582")
            .walletAddress(request.getWalletAddress())
            .build();
            
        return client.balance().getAllowancesRx(allowanceRequest);
    })
    .subscribe(
        allowances -> {
            log.info("Reactive chain completed!");
            log.info("Found {} token allowances", allowances.size());
        },
        error -> log.error("Reactive balance chain failed", error)
    );
```

## Error Handling

The SDK provides structured error handling for all programming approaches:

### Synchronous Error Handling
```java
try {
    QuoteResponse quote = client.swap().getQuote(quoteRequest);
} catch (OneInchApiException e) {
    log.error("API Error: {} (Status: {}, Request ID: {})", 
        e.getDescription(), e.getStatusCode(), e.getRequestId());
} catch (OneInchException e) {
    log.error("SDK Error: {}", e.getMessage());
}
```

### Reactive Error Handling
```java
client.swap().getQuoteRx(quoteRequest)
    .doOnError(error -> {
        if (error instanceof OneInchApiException) {
            OneInchApiException apiError = (OneInchApiException) error;
            log.error("API Error: {}", apiError.getDescription());
        } else if (error instanceof OneInchException) {
            log.error("SDK Error: {}", error.getMessage());
        }
    })
    .subscribe(
        quote -> log.info("Success: {}", quote.getDstAmount()),
        error -> log.error("Failed", error)
    );
```

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Building

```bash
git clone https://github.com/1inch/1inch-java-sdk.git
cd 1inch-java-sdk
mvn clean install
```

## Testing

### Unit Tests
Run the mock unit tests (no API key required):
```bash
# Test all modules
mvn test

# Test only core SDK
mvn test -pl oneinch-sdk-core
```

### Integration Tests
To run integration tests with real API calls:

1. **Set up API key configuration**:
   ```bash
   # Copy the sample configuration
   cp test.properties.sample test.properties
   
   # Edit test.properties and add your real API key
   nano test.properties
   ```

2. **Add your API key to test.properties**:
   ```properties
   ONEINCH_API_KEY=your-actual-api-key-here
   ```

3. **Run integration tests**:
   ```bash
   # Run only integration tests
   mvn test -pl oneinch-sdk-core -Dtest=OneInchIntegrationTest
   
   # Or run all tests (unit + integration) 
   mvn test
   ```

**Note**: `test.properties` is gitignored to prevent accidental commits of API keys. Always use `test.properties.sample` as a reference for the expected format.

## License

This project is licensed under the MIT License.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Support

For issues and questions:
- [GitHub Issues](https://github.com/1inch/1inch-java-sdk/issues)
- [1inch Developer Portal](https://portal.1inch.dev/)
- [1inch Discord](https://discord.gg/1inch)