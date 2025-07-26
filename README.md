# 1inch Java SDK

A comprehensive Java SDK for the 1inch DEX Aggregation Protocol, providing easy integration with 1inch's swap, token, token-details, and orderbook services.

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

### Swap API (`client.swap()`)
- ✅ `getQuote()` - Find the best quote to swap
- ✅ `getSwap()` - Generate calldata for swap execution
- ✅ `getSpender()` - Get 1inch Router address
- ✅ `getApproveTransaction()` - Generate token approval calldata
- ✅ `getAllowance()` - Check token allowance

### Token API (`client.token()`)
- ✅ `getMultiChainTokens()` - Get whitelisted tokens across all chains
- ✅ `getTokenList()` - Get chain-specific token list in standard format
- ✅ `getTokens()` - Get whitelisted tokens for specific chain
- ✅ `searchMultiChainTokens()` - Search tokens across multiple chains
- ✅ `getCustomTokens()` - Get token info for multiple custom addresses
- ✅ `getCustomToken()` - Get token info for single custom address

### Token Details API (`client.tokenDetails()`)
- ✅ `getTokenDetails()` - Get token details with pricing data
- ✅ `getTokenChart()` - Get token price chart data
- ✅ `getTokenPriceChange()` - Get token price changes over time
- ✅ `getNativeTokenDetails()` - Get native token details with pricing
- ✅ `getNativeTokenChart()` - Get native token chart data
- ✅ `getNativeTokenChartByRange()` - Get native token chart for date range
- ✅ `getNativeTokenChartByInterval()` - Get native token chart by interval
- ✅ `getNativeTokenPriceChange()` - Get native token price changes
- ✅ `getMultiChainTokenPriceChange()` - Get multi-chain token price changes

### Orderbook API (`client.orderbook()`)
- ✅ `createLimitOrder()` - Create a new limit order
- ✅ `getLimitOrdersByAddress()` - Get orders for specific address
- ✅ `getOrderByOrderHash()` - Get order by hash
- ✅ `getAllLimitOrders()` - Get all limit orders with filters
- ✅ `getOrdersCount()` - Get count of orders by filters
- ✅ `getEventsByOrderHash()` - Get events for specific order
- ✅ `getAllEvents()` - Get all order events
- ✅ `hasActiveOrdersWithPermit()` - Check active orders with permit
- ✅ `getUniqueActivePairs()` - Get unique active trading pairs

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