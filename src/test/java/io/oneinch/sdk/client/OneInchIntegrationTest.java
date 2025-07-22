package io.oneinch.sdk.client;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.oneinch.sdk.service.SwapService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

/**
 * Integration tests that make real API calls to 1inch.
 * These tests require a valid API key to be set in test.properties file.
 * 
 * To run these tests:
 * 1. Copy test.properties.sample to test.properties
 * 2. Add your real 1inch API key to test.properties
 * 3. Run: mvn test -Dtest=OneInchIntegrationTest
 */
@Slf4j
class OneInchIntegrationTest {
    
    private OneInchClient client;
    
    @BeforeEach
    void setUp() {
        // This will use the ONEINCH_API_KEY from system properties (loaded from test.properties)
        try {
            client = OneInchClient.builder().build();
        } catch (IllegalArgumentException e) {
            // API key not available, tests will be skipped
            log.warn("No API key available for integration tests: {}", e.getMessage());
        }
    }
    
    
    @Test
    void testRealQuoteRequest() throws OneInchException {
        SwapService swapService = client.swap();
        
        // ETH to 1INCH quote request
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")  // ETH
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")  // 1INCH token
                .amount(new BigInteger("1000000000000000000"))  // 1 ETH in wei
                .includeTokensInfo(true)
                .includeGas(true)
                .build();
        
        log.info("Making real quote request for 1 ETH to 1INCH...");
        QuoteResponse response = swapService.getQuote(request);
        
        assertNotNull(response, "Quote response should not be null");
        assertNotNull(response.getDstAmount(), "Destination amount should not be null");
        assertTrue(response.getDstAmount().compareTo(BigInteger.ZERO) > 0, "Destination amount should be positive");
        
        if (response.getDstToken() != null) {
            assertEquals("1INCH", response.getDstToken().getSymbol(), "Destination token should be 1INCH");
        }
        
        log.info("Quote successful: {} 1INCH tokens for 1 ETH", response.getDstAmount());
        
        if (response.getGas() != null) {
            log.info("Estimated gas: {}", response.getGas());
        }
    }
    
    @Test
    void testRealSpenderRequest() throws OneInchException {
        SwapService swapService = client.swap();
        
        log.info("Getting 1inch router spender address...");
        SpenderResponse response = swapService.getSpender();
        
        assertNotNull(response, "Spender response should not be null");
        assertNotNull(response.getAddress(), "Spender address should not be null");
        assertTrue(response.getAddress().startsWith("0x"), "Spender address should be a valid Ethereum address");
        assertEquals(42, response.getAddress().length(), "Ethereum address should be 42 characters long");
        
        log.info("Spender address: {}", response.getAddress());
    }
    
    @Test
    void testRealAllowanceRequest() throws OneInchException {
        SwapService swapService = client.swap();
        
        // Check allowance for a common token (1INCH) and wallet
        AllowanceRequest request = AllowanceRequest.builder()
                .tokenAddress("0x111111111117dc0aa78b770fa6a738034120c302")  // 1INCH token
                .walletAddress("0x1111111254eeb25477b68fb85ed929f73a960582")  // 1inch router address as example
                .build();
        
        log.info("Checking real token allowance...");
        AllowanceResponse response = swapService.getAllowance(request);
        
        assertNotNull(response, "Allowance response should not be null");
        assertNotNull(response.getAllowance(), "Allowance value should not be null");
        // Allowance can be 0, so we just check it's a valid number
        assertTrue(response.getAllowance().compareTo(BigInteger.ZERO) >= 0, "Allowance should be a valid non-negative number");
        
        log.info("Token allowance: {}", response.getAllowance());
    }
    
    @Test
    void testReactiveQuoteRequest() {
        SwapService swapService = client.swap();
        
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")  // ETH
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")  // 1INCH token
                .amount(new BigInteger("100000000000000000"))  // 0.1 ETH in wei
                .build();
        
        log.info("Making reactive quote request for 0.1 ETH to 1INCH...");
        
        QuoteResponse response = swapService.getQuoteRx(request)
                .doOnSuccess(quote -> log.info("Reactive quote successful: {} 1INCH tokens", quote.getDstAmount()))
                .doOnError(error -> log.error("Reactive quote failed", error))
                .blockingGet();
        
        assertNotNull(response, "Reactive quote response should not be null");
        assertNotNull(response.getDstAmount(), "Destination amount should not be null");
        assertTrue(response.getDstAmount().compareTo(BigInteger.ZERO) > 0, "Destination amount should be positive");
    }
    
    @Test
    void testAsyncQuoteRequest() throws Exception {
        SwapService swapService = client.swap();
        
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")  // ETH
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")  // 1INCH token
                .amount(new BigInteger("500000000000000000"))  // 0.5 ETH in wei
                .build();
        
        log.info("Making async quote request for 0.5 ETH to 1INCH...");
        
        QuoteResponse response = swapService.getQuoteAsync(request)
                .whenComplete((quote, throwable) -> {
                    if (throwable != null) {
                        log.error("Async quote failed", throwable);
                    } else {
                        log.info("Async quote successful: {} 1INCH tokens", quote.getDstAmount());
                    }
                })
                .get();
        
        assertNotNull(response, "Async quote response should not be null");
        assertNotNull(response.getDstAmount(), "Destination amount should not be null");
        assertTrue(response.getDstAmount().compareTo(BigInteger.ZERO) > 0, "Destination amount should be positive");
    }
}