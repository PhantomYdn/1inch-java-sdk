package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchPriceApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.price.CurrenciesResponse;
import io.oneinch.sdk.model.price.Currency;
import io.oneinch.sdk.model.price.PostPriceRequest;
import io.oneinch.sdk.model.price.PriceRequest;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceImplTest {

    @Mock
    private OneInchPriceApiService apiService;

    private PriceServiceImpl priceService;

    private static final Integer CHAIN_ID = 1; // Ethereum
    private static final String ETH_ADDRESS = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
    private static final String USDC_ADDRESS = "0xa0b86a33e6ab6b6ce4e5a5b7db2e8df6b1d2b9c7";
    private static final BigInteger ETH_PRICE = new BigInteger("1000000000000000000"); // 1 ETH in wei
    private static final BigInteger USDC_PRICE = new BigInteger("1000000000000000000"); // 1 USDC in wei

    @BeforeEach
    void setUp() {
        priceService = new PriceServiceImpl(apiService);
    }

    // ==================== WHITELIST PRICES TESTS ====================

    @Test
    void testGetWhitelistPricesRx_Success() {
        // Given
        Map<String, BigInteger> expectedPrices = Map.of(
                ETH_ADDRESS, ETH_PRICE,
                USDC_ADDRESS, USDC_PRICE
        );
        when(apiService.getWhitelistPrices(CHAIN_ID, "USD"))
                .thenReturn(Single.just(expectedPrices));

        // When
        Map<String, BigInteger> result = priceService.getWhitelistPricesRx(CHAIN_ID, Currency.USD)
                .blockingGet();

        // Then
        assertEquals(expectedPrices, result);
        verify(apiService).getWhitelistPrices(CHAIN_ID, "USD");
    }

    @Test
    void testGetWhitelistPricesRx_NullCurrency() {
        // Given
        Map<String, BigInteger> expectedPrices = Map.of(ETH_ADDRESS, ETH_PRICE);
        when(apiService.getWhitelistPrices(CHAIN_ID, null))
                .thenReturn(Single.just(expectedPrices));

        // When
        Map<String, BigInteger> result = priceService.getWhitelistPricesRx(CHAIN_ID, null)
                .blockingGet();

        // Then
        assertEquals(expectedPrices, result);
        verify(apiService).getWhitelistPrices(CHAIN_ID, null);
    }

    @Test
    void testGetWhitelistPricesRx_Error() {
        // Given
        RuntimeException exception = new RuntimeException("API Error");
        when(apiService.getWhitelistPrices(CHAIN_ID, "USD"))
                .thenReturn(Single.error(exception));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                priceService.getWhitelistPricesRx(CHAIN_ID, Currency.USD).blockingGet());
    }

    @Test
    void testGetWhitelistPricesAsync() throws Exception {
        // Given
        Map<String, BigInteger> expectedPrices = Map.of(ETH_ADDRESS, ETH_PRICE);
        when(apiService.getWhitelistPrices(CHAIN_ID, "EUR"))
                .thenReturn(Single.just(expectedPrices));

        // When
        CompletableFuture<Map<String, BigInteger>> future = 
                priceService.getWhitelistPricesAsync(CHAIN_ID, Currency.EUR);
        Map<String, BigInteger> result = future.get();

        // Then
        assertEquals(expectedPrices, result);
        verify(apiService).getWhitelistPrices(CHAIN_ID, "EUR");
    }

    @Test
    void testGetWhitelistPrices_Synchronous() throws OneInchException {
        // Given
        Map<String, BigInteger> expectedPrices = Map.of(ETH_ADDRESS, ETH_PRICE);
        when(apiService.getWhitelistPrices(CHAIN_ID, "JPY"))
                .thenReturn(Single.just(expectedPrices));

        // When
        Map<String, BigInteger> result = priceService.getWhitelistPrices(CHAIN_ID, Currency.JPY);

        // Then
        assertEquals(expectedPrices, result);
        verify(apiService).getWhitelistPrices(CHAIN_ID, "JPY");
    }

    // ==================== SPECIFIC TOKEN PRICES TESTS ====================

    @Test
    void testGetPricesRx_EmptyAddresses_CallsWhitelistApi() {
        // Given
        PriceRequest request = PriceRequest.builder()
                .chainId(CHAIN_ID)
                .addresses(List.of())
                .currency(Currency.USD)
                .build();
        
        Map<String, BigInteger> expectedPrices = Map.of(ETH_ADDRESS, ETH_PRICE);
        when(apiService.getWhitelistPrices(CHAIN_ID, "USD"))
                .thenReturn(Single.just(expectedPrices));

        // When
        Map<String, BigInteger> result = priceService.getPricesRx(request).blockingGet();

        // Then
        assertEquals(expectedPrices, result);
        verify(apiService).getWhitelistPrices(CHAIN_ID, "USD");
        verifyNoMoreInteractions(apiService);
    }

    @Test
    void testGetPricesRx_SingleAddress_UsesGetMethod() {
        // Given
        PriceRequest request = PriceRequest.builder()
                .chainId(CHAIN_ID)
                .addresses(List.of(ETH_ADDRESS))
                .currency(Currency.USD)
                .build();
        
        Map<String, BigInteger> expectedPrices = Map.of(ETH_ADDRESS, ETH_PRICE);
        when(apiService.getPricesByAddresses(CHAIN_ID, ETH_ADDRESS, "USD"))
                .thenReturn(Single.just(expectedPrices));

        // When
        Map<String, BigInteger> result = priceService.getPricesRx(request).blockingGet();

        // Then
        assertEquals(expectedPrices, result);
        verify(apiService).getPricesByAddresses(CHAIN_ID, ETH_ADDRESS, "USD");
    }

    @Test
    void testGetPricesRx_MultipleAddresses_UsesPostMethod() {
        // Given
        List<String> addresses = Arrays.asList(ETH_ADDRESS, USDC_ADDRESS);
        PriceRequest request = PriceRequest.builder()
                .chainId(CHAIN_ID)
                .addresses(addresses)
                .currency(Currency.USD)
                .build();
        
        Map<String, BigInteger> expectedPrices = Map.of(
                ETH_ADDRESS, ETH_PRICE,
                USDC_ADDRESS, USDC_PRICE
        );
        
        when(apiService.getPricesByPost(eq(CHAIN_ID), any(PostPriceRequest.class)))
                .thenReturn(Single.just(expectedPrices));

        // When
        Map<String, BigInteger> result = priceService.getPricesRx(request).blockingGet();

        // Then
        assertEquals(expectedPrices, result);
        verify(apiService).getPricesByPost(eq(CHAIN_ID), argThat(postRequest -> 
                postRequest.getTokens().equals(addresses) && 
                "USD".equals(postRequest.getCurrency())
        ));
    }

    @Test
    void testGetPricesAsync() throws Exception {
        // Given
        PriceRequest request = PriceRequest.builder()
                .chainId(CHAIN_ID)
                .addresses(List.of(ETH_ADDRESS))
                .currency(Currency.EUR)
                .build();
        
        Map<String, BigInteger> expectedPrices = Map.of(ETH_ADDRESS, ETH_PRICE);
        when(apiService.getPricesByAddresses(CHAIN_ID, ETH_ADDRESS, "EUR"))
                .thenReturn(Single.just(expectedPrices));

        // When
        CompletableFuture<Map<String, BigInteger>> future = priceService.getPricesAsync(request);
        Map<String, BigInteger> result = future.get();

        // Then
        assertEquals(expectedPrices, result);
    }

    @Test
    void testGetPrices_Synchronous() throws OneInchException {
        // Given
        PriceRequest request = PriceRequest.builder()
                .chainId(CHAIN_ID)
                .addresses(List.of(ETH_ADDRESS))
                .currency(Currency.JPY)
                .build();
        
        Map<String, BigInteger> expectedPrices = Map.of(ETH_ADDRESS, ETH_PRICE);
        when(apiService.getPricesByAddresses(CHAIN_ID, ETH_ADDRESS, "JPY"))
                .thenReturn(Single.just(expectedPrices));

        // When
        Map<String, BigInteger> result = priceService.getPrices(request);

        // Then
        assertEquals(expectedPrices, result);
    }

    // ==================== SINGLE TOKEN PRICE TESTS ====================

    @Test
    void testGetPriceRx_Success() {
        // Given
        Map<String, BigInteger> priceMap = Map.of(ETH_ADDRESS, ETH_PRICE);
        when(apiService.getPricesByAddresses(CHAIN_ID, ETH_ADDRESS, "USD"))
                .thenReturn(Single.just(priceMap));

        // When
        BigInteger result = priceService.getPriceRx(CHAIN_ID, ETH_ADDRESS, Currency.USD)
                .blockingGet();

        // Then
        assertEquals(ETH_PRICE, result);
        verify(apiService).getPricesByAddresses(CHAIN_ID, ETH_ADDRESS, "USD");
    }

    @Test
    void testGetPriceRx_NotFound() {
        // Given
        Map<String, BigInteger> emptyMap = Map.of();
        when(apiService.getPricesByAddresses(CHAIN_ID, ETH_ADDRESS, "USD"))
                .thenReturn(Single.just(emptyMap));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
                priceService.getPriceRx(CHAIN_ID, ETH_ADDRESS, Currency.USD).blockingGet());
        
        // Verify the cause is OneInchException
        assertTrue(exception.getCause() instanceof OneInchException);
        assertTrue(exception.getCause().getMessage().contains("Price not found for address"));
    }

    @Test
    void testGetPriceAsync() throws Exception {
        // Given
        Map<String, BigInteger> priceMap = Map.of(ETH_ADDRESS, ETH_PRICE);
        when(apiService.getPricesByAddresses(CHAIN_ID, ETH_ADDRESS, "EUR"))
                .thenReturn(Single.just(priceMap));

        // When
        CompletableFuture<BigInteger> future = priceService.getPriceAsync(CHAIN_ID, ETH_ADDRESS, Currency.EUR);
        BigInteger result = future.get();

        // Then
        assertEquals(ETH_PRICE, result);
    }

    @Test
    void testGetPrice_Synchronous() throws OneInchException {
        // Given
        Map<String, BigInteger> priceMap = Map.of(ETH_ADDRESS, ETH_PRICE);
        when(apiService.getPricesByAddresses(CHAIN_ID, ETH_ADDRESS, "JPY"))
                .thenReturn(Single.just(priceMap));

        // When
        BigInteger result = priceService.getPrice(CHAIN_ID, ETH_ADDRESS, Currency.JPY);

        // Then
        assertEquals(ETH_PRICE, result);
    }

    // ==================== SUPPORTED CURRENCIES TESTS ====================

    @Test
    void testGetSupportedCurrenciesRx_Success() {
        // Given
        List<String> expectedCurrencies = Arrays.asList("USD", "EUR", "JPY", "GBP");
        CurrenciesResponse response = new CurrenciesResponse();
        response.setCodes(expectedCurrencies);
        
        when(apiService.getSupportedCurrencies(CHAIN_ID))
                .thenReturn(Single.just(response));

        // When
        List<String> result = priceService.getSupportedCurrenciesRx(CHAIN_ID).blockingGet();

        // Then
        assertEquals(expectedCurrencies, result);
        verify(apiService).getSupportedCurrencies(CHAIN_ID);
    }

    @Test
    void testGetSupportedCurrenciesRx_Error() {
        // Given
        RuntimeException exception = new RuntimeException("API Error");
        when(apiService.getSupportedCurrencies(CHAIN_ID))
                .thenReturn(Single.error(exception));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                priceService.getSupportedCurrenciesRx(CHAIN_ID).blockingGet());
    }

    @Test
    void testGetSupportedCurrenciesAsync() throws Exception {
        // Given
        List<String> expectedCurrencies = Arrays.asList("USD", "EUR");
        CurrenciesResponse response = new CurrenciesResponse();
        response.setCodes(expectedCurrencies);
        
        when(apiService.getSupportedCurrencies(CHAIN_ID))
                .thenReturn(Single.just(response));

        // When
        CompletableFuture<List<String>> future = priceService.getSupportedCurrenciesAsync(CHAIN_ID);
        List<String> result = future.get();

        // Then
        assertEquals(expectedCurrencies, result);
    }

    @Test
    void testGetSupportedCurrencies_Synchronous() throws OneInchException {
        // Given
        List<String> expectedCurrencies = Arrays.asList("USD", "EUR", "JPY");
        CurrenciesResponse response = new CurrenciesResponse();
        response.setCodes(expectedCurrencies);
        
        when(apiService.getSupportedCurrencies(CHAIN_ID))
                .thenReturn(Single.just(response));

        // When
        List<String> result = priceService.getSupportedCurrencies(CHAIN_ID);

        // Then
        assertEquals(expectedCurrencies, result);
    }
}