# 1inch Java SDK

A comprehensive Java SDK for the 1inch DEX Aggregation Protocol, providing easy integration with 1inch's swap services.

## Features

- ✅ Java 11 compatible
- ✅ Interface-driven design for easy testing and mocking
- ✅ Both synchronous and asynchronous API methods
- ✅ Comprehensive error handling
- ✅ Built-in logging with SLF4J
- ✅ Full Swap API support
- ✅ Type-safe models with Jackson
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

## Quick Start

```java
import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.model.QuoteRequest;
import io.oneinch.sdk.model.QuoteResponse;

// Initialize the client with your API key
OneInchClient client = OneInchClient.builder()
    .apiKey("your-api-key-here")
    .build();

// Get a quote for swapping ETH to 1INCH
QuoteRequest quoteRequest = QuoteRequest.builder()
    .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")  // ETH
    .dst("0x111111111117dc0aa78b770fa6a738034120c302")  // 1INCH
    .amount("10000000000000000")  // 0.01 ETH in wei
    .build();

QuoteResponse quote = client.swap().getQuote(quoteRequest);
System.out.println("Expected output: " + quote.getDstAmount());
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

```java
OneInchClient client = OneInchClient.builder()
    .apiKey("your-api-key")
    .build();
```

### Custom HTTP Client
```java
OneInchClient client = OneInchClient.builder()
    .apiKey("your-api-key")
    .httpClient(customHttpClient)
    .build();
```

## Examples

### Complete Swap Flow
```java
// 1. Check allowance
AllowanceRequest allowanceRequest = AllowanceRequest.builder()
    .tokenAddress("0x111111111117dc0aa78b770fa6a738034120c302")
    .walletAddress("0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8")
    .build();

AllowanceResponse allowance = client.swap().getAllowance(allowanceRequest);

// 2. Approve if necessary
if (new BigInteger(allowance.getAllowance()).compareTo(new BigInteger("10000000000000000")) < 0) {
    ApproveTransactionRequest approveRequest = ApproveTransactionRequest.builder()
        .tokenAddress("0x111111111117dc0aa78b770fa6a738034120c302")
        .amount("10000000000000000")
        .build();
        
    ApproveCallDataResponse approveData = client.swap().getApproveTransaction(approveRequest);
    // Execute approve transaction...
}

// 3. Get quote
QuoteRequest quoteRequest = QuoteRequest.builder()
    .src("0x111111111117dc0aa78b770fa6a738034120c302")
    .dst("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
    .amount("10000000000000000")
    .build();

QuoteResponse quote = client.swap().getQuote(quoteRequest);

// 4. Execute swap
SwapRequest swapRequest = SwapRequest.builder()
    .src("0x111111111117dc0aa78b770fa6a738034120c302")
    .dst("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
    .amount("10000000000000000")
    .from("0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8")
    .origin("0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8")
    .slippage(1.0)
    .build();

SwapResponse swap = client.swap().getSwap(swapRequest);
// Execute swap transaction using swap.getTx()...
```

### Async Operations
```java
CompletableFuture<QuoteResponse> quoteFuture = client.swap().getQuoteAsync(quoteRequest);
quoteFuture.thenAccept(quote -> {
    System.out.println("Async quote result: " + quote.getDstAmount());
});
```

## Error Handling

The SDK provides structured error handling:

```java
try {
    QuoteResponse quote = client.swap().getQuote(quoteRequest);
} catch (OneInchApiException e) {
    System.err.println("API Error: " + e.getDescription());
    System.err.println("Status Code: " + e.getStatusCode());
    System.err.println("Request ID: " + e.getRequestId());
} catch (OneInchException e) {
    System.err.println("SDK Error: " + e.getMessage());
}
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

```bash
mvn test
```

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