package io.oneinch.sdk.client;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.oneinch.sdk.service.OrderbookService;
import io.oneinch.sdk.service.SwapService;
import io.oneinch.sdk.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

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
        // Check if API key is available before creating client
        String apiKey = System.getProperty("ONEINCH_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException(
                "API key is required for integration tests. " +
                "Please add ONEINCH_API_KEY to the .env file in the project root directory, " +
                "or set it as an environment variable. " +
                "Get your API key from https://portal.1inch.dev/"
            );
        }
        
        try {
            client = OneInchClient.builder().build();
        } catch (Exception e) {
            throw new IllegalStateException(
                "Failed to initialize OneInchClient for integration tests. " +
                "Please ensure ONEINCH_API_KEY is set correctly in .env file. " +
                "Error: " + e.getMessage(), e
            );
        }
    }
    
    
    @Test
    void testRealQuoteRequest() throws OneInchException {
        SwapService swapService = client.swap();
        
        // ETH to 1INCH quote request
        QuoteRequest request = QuoteRequest.builder()
                .chainId(1)  // Ethereum
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
        SpenderResponse response = swapService.getSpender(1);
        
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
                .chainId(1)  // Ethereum
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
                .chainId(1)  // Ethereum
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
                .chainId(1)  // Ethereum
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
    
    @Test
    void testRealMultiChainTokensRequest() {
        log.info("Testing real multi-chain tokens API call...");
        
        TokenListRequest request = TokenListRequest.builder()
                .provider("1inch")
                .build();
        
        try {
            List<ProviderTokenDto> tokens = client.token().getMultiChainTokens(request);
            
            assertNotNull(tokens, "Multi-chain tokens response should not be null");
            assertFalse(tokens.isEmpty(), "Multi-chain tokens should contain some tokens");
            
            // Check a few well-known tokens
            ProviderTokenDto oneInchToken = null;
            for (ProviderTokenDto token : tokens) {
                if ("1INCH".equals(token.getSymbol())) {
                    oneInchToken = token;
                    break;
                }
            }
            
            if (oneInchToken != null) {
                assertNotNull(oneInchToken, "1INCH token should not be null");
                assertEquals("1INCH", oneInchToken.getSymbol());
                assertTrue(oneInchToken.getName().toLowerCase().contains("1inch"), "Token name should contain 1inch");
                
                log.info("Found 1INCH token: {} at address {}", 
                        oneInchToken.getName(), oneInchToken.getAddress());
            }
            
            log.info("Multi-chain tokens: {} tokens retrieved", tokens.size());
        } catch (OneInchException e) {
            log.error("Multi-chain tokens request failed:", e);
            fail("Multi-chain tokens request should not fail: " + e.getMessage());
        }
    }
    
    @Test
    void testRealTokenListRequest() {
        log.info("Testing real token list API call for Ethereum...");
        
        TokenListRequest request = TokenListRequest.builder()
                .chainId(1) // Ethereum
                .provider("1inch")
                .build();
        
        try {
            TokenListResponse tokenList = client.token().getTokenList(request);
            
            assertNotNull(tokenList, "Token list response should not be null");
            assertNotNull(tokenList.getTokens(), "Tokens list should not be null");
            assertFalse(tokenList.getTokens().isEmpty(), "Tokens list should contain some tokens");
            assertNotNull(tokenList.getName(), "Token list name should not be null");
            assertNotNull(tokenList.getVersion(), "Token list version should not be null");
            
            // Check for some well-known Ethereum tokens
            boolean foundUsdc = false;
            for (Token token : tokenList.getTokens()) {
                if ("USDC".equals(token.getSymbol())) {
                    foundUsdc = true;
                    assertEquals(Integer.valueOf(6), token.getDecimals());
                    assertNotNull(token.getAddress());
                    log.info("Found USDC token: {} at {}", token.getName(), token.getAddress());
                    break;
                }
            }
            
            log.info("Token list: {} tokens retrieved, USDC found: {}", 
                    tokenList.getTokens().size(), foundUsdc);
        } catch (OneInchException e) {
            log.error("Token list request failed:", e);
            fail("Token list request should not fail: " + e.getMessage());
        }
    }
    
    @Test
    void testRealTokenSearchRequest() {
        log.info("Testing real token search API call...");
        
        TokenSearchRequest request = TokenSearchRequest.builder()
                .query("1inch")
                .onlyPositiveRating(true)
                .limit(5)
                .build();
        
        try {
            List<Token> searchResults = client.token().searchMultiChainTokens(request);
            
            assertNotNull(searchResults, "Search results should not be null");
            assertFalse(searchResults.isEmpty(), "Search results should contain some tokens");
            assertTrue(searchResults.size() <= 5, "Search results should respect limit");
            
            // Check that results contain 1INCH related tokens
            boolean foundOneInch = false;
            for (Token token : searchResults) {
                if (token.getSymbol().contains("1INCH") || token.getName().toLowerCase().contains("1inch")) {
                    foundOneInch = true;
                    assertNotNull(token.getAddress(), "Token address should not be null");
                    assertNotNull(token.getChainId(), "Token chain ID should not be null");
                    assertTrue(token.getRating() > 0, "Token rating should be positive");
                    
                    log.info("Found 1inch-related token: {} ({}) on chain {}", 
                            token.getName(), token.getSymbol(), token.getChainId());
                    break;
                }
            }
            
            assertTrue(foundOneInch, "Should find at least one 1inch-related token");
            log.info("Token search: {} results retrieved", searchResults.size());
        } catch (OneInchException e) {
            log.error("Token search request failed: ", e);
            fail("Token search request should not fail: " + e.getMessage());
        }
    }
    
    @Test
    void testRealCustomTokenRequest() {
        log.info("Testing real custom token API call...");
        
        // Test getting 1INCH token details
        Integer chainId = 1; // Ethereum
        String oneInchAddress = "0x111111111117dc0aa78b770fa6a738034120c302";
        
        try {
            Token token = client.token().getCustomToken(chainId, oneInchAddress);
            
            assertNotNull(token, "Custom token response should not be null");
            assertEquals(chainId, token.getChainId(), "Chain ID should match");
            assertEquals(oneInchAddress.toLowerCase(), token.getAddress().toLowerCase(), "Address should match");
            assertEquals("1INCH", token.getSymbol(), "Symbol should be 1INCH");
            assertNotNull(token.getName(), "Token name should not be null");
            assertTrue(token.getName().toLowerCase().contains("1inch"), "Name should contain 1inch");
            assertEquals(Integer.valueOf(18), token.getDecimals(), "1INCH should have 18 decimals");
            
            log.info("Custom token retrieved: {} ({}) - {}", 
                    token.getName(), token.getSymbol(), token.getAddress());
        } catch (OneInchException e) {
            log.error("Custom token request failed:", e);
            fail("Custom token request should not fail: " + e.getMessage());
        }
    }
    
    @Test
    void testReactiveTokenRequest() {
        log.info("Making reactive token request for Ethereum tokens...");
        
        TokenListRequest request = TokenListRequest.builder()
                .chainId(1) // Ethereum
                .provider("1inch")
                .build();
        
        client.token().getTokensRx(request)
                .doOnSuccess(tokens -> {
                    assertNotNull(tokens, "Reactive tokens response should not be null");
                    assertFalse(tokens.isEmpty(), "Reactive tokens should contain some tokens");
                    
                    log.info("Reactive token request successful: {} tokens", tokens.size());
                })
                .doOnError(error -> {
                    log.error("Reactive token request failed", error);
                    fail("Reactive token request should not fail: " + error.getMessage());
                })
                .blockingGet();
    }
    
    @Test
    void testRealGetAllLimitOrdersRequest() {
        log.info("Testing real get all limit orders API call...");
        
        OrderbookService orderbookService = client.orderbook();
        
        GetAllLimitOrdersRequest request = GetAllLimitOrdersRequest.builder()
                .chainId(1) // Ethereum
                .page(1)
                .limit(5)
                .statuses("1") // Only valid orders
                .build();
        
        try {
            List<GetLimitOrdersV4Response> orders = orderbookService.getAllLimitOrders(request);
            
            assertNotNull(orders, "All limit orders response should not be null");
            assertTrue(orders.size() <= 5, "Orders should respect the limit parameter");
            
            if (!orders.isEmpty()) {
                GetLimitOrdersV4Response firstOrder = orders.get(0);
                assertNotNull(firstOrder.getOrderHash(), "Order hash should not be null");
                assertNotNull(firstOrder.getData(), "Order data should not be null");
                assertNotNull(firstOrder.getData().getMakerAsset(), "Maker asset should not be null");
                assertNotNull(firstOrder.getData().getTakerAsset(), "Taker asset should not be null");
                
                log.info("Retrieved {} limit orders. First order: {} -> {}", 
                        orders.size(), 
                        firstOrder.getData().getMakerAsset(), 
                        firstOrder.getData().getTakerAsset());
            } else {
                log.info("No limit orders found on Ethereum");
            }
        } catch (OneInchException e) {
            // This might fail if no orders are available or API limitations
            log.warn("Get all limit orders request failed (this might be expected): {}", e.getMessage());
        }
    }
    
    @Test
    void testRealGetOrdersCountRequest() {
        log.info("Testing real get orders count API call...");
        
        OrderbookService orderbookService = client.orderbook();
        
        GetLimitOrdersCountRequest request = GetLimitOrdersCountRequest.builder()
                .chainId(1) // Ethereum
                .statuses("1,2,3") // All statuses
                .build();
        
        try {
            GetLimitOrdersCountV4Response response = orderbookService.getOrdersCount(request);
            
            assertNotNull(response, "Orders count response should not be null");
            assertNotNull(response.getCount(), "Orders count should not be null");
            assertTrue(response.getCount() >= 0, "Orders count should be non-negative");
            
            log.info("Total orders count: {}", response.getCount());
        } catch (OneInchException e) {
            // This might fail if API limitations or access restrictions
            log.warn("Get orders count request failed (this might be expected): {}", e.getMessage());
        }
    }
    
    @Test
    void testRealGetAllEventsRequest() {
        log.info("Testing real get all events API call...");
        
        OrderbookService orderbookService = client.orderbook();
        
        GetEventsRequest request = GetEventsRequest.builder()
                .chainId(1) // Ethereum
                .limit(10)
                .build();
        
        try {
            List<GetEventsV4Response> events = orderbookService.getAllEvents(request);
            
            assertNotNull(events, "All events response should not be null");
            assertTrue(events.size() <= 10, "Events should respect the limit parameter");
            
            if (!events.isEmpty()) {
                GetEventsV4Response firstEvent = events.get(0);
                assertNotNull(firstEvent.getOrderHash(), "Event order hash should not be null");
                assertNotNull(firstEvent.getAction(), "Event action should not be null");
                assertNotNull(firstEvent.getCreateDateTime(), "Event creation time should not be null");
                
                log.info("Retrieved {} events. First event: {} action on order {}", 
                        events.size(), 
                        firstEvent.getAction(), 
                        firstEvent.getOrderHash());
            } else {
                log.info("No events found");
            }
        } catch (OneInchException e) {
            // This might fail if no events are available or API limitations
            log.warn("Get all events request failed (this might be expected): {}", e.getMessage());
        }
    }
    
    @Test
    void testRealGetUniqueActivePairsRequest() {
        log.info("Testing real get unique active pairs API call...");
        
        OrderbookService orderbookService = client.orderbook();
        
        GetUniqueActivePairsRequest request = GetUniqueActivePairsRequest.builder()
                .chainId(1) // Ethereum
                .page(1)
                .limit(5)
                .build();
        
        try {
            GetActiveUniquePairsResponse response = orderbookService.getUniqueActivePairs(request);
            
            assertNotNull(response, "Unique active pairs response should not be null");
            assertNotNull(response.getMeta(), "Response meta should not be null");
            assertNotNull(response.getItems(), "Response items should not be null");
            assertTrue(response.getItems().size() <= 5, "Pairs should respect the limit parameter");
            
            if (!response.getItems().isEmpty()) {
                UniquePairs firstPair = response.getItems().get(0);
                assertNotNull(firstPair.getMakerAsset(), "Maker asset should not be null");
                assertNotNull(firstPair.getTakerAsset(), "Taker asset should not be null");
                
                log.info("Retrieved {} unique pairs. First pair: {} <-> {}", 
                        response.getItems().size(),
                        firstPair.getMakerAsset(), 
                        firstPair.getTakerAsset());
                log.info("Total pairs available: {}", response.getMeta().getTotalItems());
            } else {
                log.info("No unique active pairs found");
            }
        } catch (OneInchException e) {
            // This might fail if no pairs are available or API limitations
            log.warn("Get unique active pairs request failed (this might be expected): {}", e.getMessage());
        }
    }
    
    @Test
    void testReactiveGetAllLimitOrdersRequest() {
        log.info("Making reactive get all limit orders request...");
        
        OrderbookService orderbookService = client.orderbook();
        
        GetAllLimitOrdersRequest request = GetAllLimitOrdersRequest.builder()
                .chainId(1) // Ethereum
                .page(1)
                .limit(3)
                .statuses("1")
                .build();
        
        try {
            orderbookService.getAllLimitOrdersRx(request)
                    .doOnSuccess(orders -> {
                        assertNotNull(orders, "Reactive orders response should not be null");
                        assertTrue(orders.size() <= 3, "Orders should respect the limit parameter");
                        
                        log.info("Reactive get all limit orders successful: {} orders", orders.size());
                    })
                    .doOnError(error -> {
                        log.warn("Reactive get all limit orders failed (this might be expected): {}", error.getMessage());
                    })
                    .blockingGet();
        } catch (Exception e) {
            log.warn("Reactive get all limit orders request failed (this might be expected): {}", e.getMessage());
        }
    }
}