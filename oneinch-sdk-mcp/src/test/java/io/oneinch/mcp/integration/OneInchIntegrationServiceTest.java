package io.oneinch.mcp.integration;

import io.oneinch.mcp.cache.CacheService;
import io.oneinch.mcp.service.OneInchClientService;
import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.model.swap.QuoteRequest;
import io.oneinch.sdk.model.swap.QuoteResponse;
import io.oneinch.sdk.model.token.TokenListRequest;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import jakarta.inject.Inject;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Tests for 1inch SDK integration functionality.
 */
@QuarkusTest
class OneInchIntegrationServiceTest {

    @Inject
    OneInchIntegrationService integrationService;

    @Test
    void testServiceInjection() {
        // Test that the service is properly injected
        assertNotNull(integrationService);
    }

    @Test 
    void testGetSwapQuoteStructure() {
        // Test the structure of swap quote request
        QuoteRequest request = QuoteRequest.builder()
                .chainId(1)
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302") 
                .amount(new BigInteger("1000000000000000"))
                .build();
        
        assertNotNull(request);
        assertEquals(Integer.valueOf(1), request.getChainId());
        assertNotNull(request.getAmount());
    }

    @Test
    void testTokenListRequestStructure() {
        // Test the structure of token list request
        TokenListRequest request = TokenListRequest.builder()
                .chainId(1)
                .provider("1inch")
                .build();
        
        assertNotNull(request);
        assertEquals(Integer.valueOf(1), request.getChainId());
        assertEquals("1inch", request.getProvider());
    }

    @Test
    void testAsyncMethodSignatures() {
        // Test that async methods return CompletableFuture
        QuoteRequest quoteRequest = QuoteRequest.builder()
                .chainId(1)
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount(new BigInteger("1000000000000000"))
                .build();

        // This will fail if no valid API key is configured, but tests the method signature
        CompletableFuture<QuoteResponse> future = integrationService.getSwapQuote(quoteRequest);
        assertNotNull(future);
        
        // Test that it's indeed a CompletableFuture
        assertTrue(future instanceof CompletableFuture);
    }

    @Test
    void testRateLimitingAnnotationsPresent() {
        // Verify that rate limiting annotations are present on service methods
        try {
            var method = OneInchIntegrationService.class.getMethod("getSwapQuote", QuoteRequest.class);
            var rateLimitAnnotation = method.getAnnotation(
                io.oneinch.mcp.ratelimit.RateLimitInterceptor.RateLimit.class
            );
            assertNotNull(rateLimitAnnotation, "Rate limiting annotation should be present");
            assertEquals("swap-quote", rateLimitAnnotation.clientId());
        } catch (NoSuchMethodException e) {
            fail("getSwapQuote method should exist");
        }
    }
}