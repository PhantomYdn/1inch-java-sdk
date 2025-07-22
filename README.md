# 1inch Java SDK

A comprehensive Java SDK for the 1inch DEX Aggregation Protocol, providing easy integration with 1inch's swap services.

## Features

- ✅ Java 11 compatible
- ✅ Modern reactive programming with RxJava 3
- ✅ Interface-driven design for easy testing and mocking
- ✅ Multiple programming approaches: reactive, synchronous, and asynchronous
- ✅ OkHttp with HTTP/2 support and connection pooling
- ✅ Type-safe REST API integration with Retrofit 2
- ✅ Comprehensive error handling
- ✅ Built-in logging with SLF4J
- ✅ Full Swap API support
- ✅ Type-safe models with Jackson and BigInteger precision
- ✅ Extensive unit test coverage

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.oneinch.sdk</groupId>
    <artifactId>oneinch-sdk</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
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

### Swap API
- ✅ `getQuote()` - Find the best quote to swap
- ✅ `getSwap()` - Generate calldata for swap execution
- ✅ `getSpender()` - Get 1inch Router address
- ✅ `getApproveTransaction()` - Generate token approval calldata
- ✅ `getAllowance()` - Check token allowance
- ✅ `getLiquiditySources()` - List available protocols
- ✅ `getTokens()` - List supported tokens

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

### Reactive Swap Flow
```java
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.math.BigInteger;

try (OneInchClient client = OneInchClient.builder()
        .build()) {  // Uses ONEINCH_API_KEY environment variable
    
    BigInteger swapAmount = new BigInteger("10000000000000000"); // 0.01 ETH in wei
    String walletAddress = "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8";
    
    // Reactive chaining with proper error handling
    client.swap().getSpenderRx()
        .doOnSuccess(spender -> log.info("Spender: {}", spender.getAddress()))
        .flatMap(spender -> {
            // Check allowance
            AllowanceRequest allowanceRequest = AllowanceRequest.builder()
                .tokenAddress("0x111111111117dc0aa78b770fa6a738034120c302")
                .walletAddress(walletAddress)
                .build();
            return client.swap().getAllowanceRx(allowanceRequest);
        })
        .flatMap(allowance -> {
            // Get quote
            QuoteRequest quoteRequest = QuoteRequest.builder()
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
Single<SpenderResponse> spenderSingle = client.swap().getSpenderRx()
    .subscribeOn(Schedulers.io());

Single<QuoteResponse> quoteSingle = client.swap().getQuoteRx(quoteRequest)
    .subscribeOn(Schedulers.io());

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
        .tokenAddress("0x111111111117dc0aa78b770fa6a738034120c302")
        .walletAddress("0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8")
        .build();
    
    AllowanceResponse allowance = client.swap().getAllowance(allowanceRequest);
    
    // 2. Get quote
    QuoteRequest quoteRequest = QuoteRequest.builder()
        .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
        .dst("0x111111111117dc0aa78b770fa6a738034120c302")
        .amount(new BigInteger("10000000000000000"))
        .build();
    
    QuoteResponse quote = client.swap().getQuote(quoteRequest);
    
    // 3. Execute swap (async with CompletableFuture)
    SwapRequest swapRequest = SwapRequest.builder()
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
mvn test
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
   mvn test -Dtest=OneInchIntegrationTest
   
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