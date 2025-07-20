package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchApiService;
import io.oneinch.sdk.exception.OneInchApiException;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.HttpException;
import retrofit2.Response;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwapServiceImplTest {

    @Mock
    private OneInchApiService apiService;

    private SwapServiceImpl swapService;

    @BeforeEach
    void setUp() {
        swapService = new SwapServiceImpl(apiService);
    }

    @Test
    void testGetQuoteRx_Success() {
        // Given
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .build();

        QuoteResponse expectedResponse = new QuoteResponse();
        expectedResponse.setDstAmount("1000000000000000000");

        when(apiService.getQuote(any(), any(), any(), any(), any(), any(), 
                any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Single.just(expectedResponse));

        // When
        QuoteResponse result = swapService.getQuoteRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals("1000000000000000000", result.getDstAmount());
        verify(apiService).getQuote(eq("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"),
                eq("0x111111111117dc0aa78b770fa6a738034120c302"),
                eq("10000000000000000"), any(), any(), any(), any(), any(), any(), any(), 
                any(), any(), any(), any(), any());
    }

    @Test
    void testGetQuoteRx_WithAllParameters() {
        // Given
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .protocols("UNISWAP_V3")
                .fee(1.0)
                .gasPrice("20000000000")
                .includeTokensInfo(true)
                .includeProtocols(true)
                .includeGas(true)
                .build();

        QuoteResponse expectedResponse = new QuoteResponse();
        expectedResponse.setDstAmount("1000000000000000000");

        when(apiService.getQuote(anyString(), anyString(), anyString(), anyString(), 
                anyDouble(), anyString(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Single.just(expectedResponse));

        // When
        QuoteResponse result = swapService.getQuoteRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals("1000000000000000000", result.getDstAmount());
    }

    @Test
    void testGetSwapRx_Success() {
        // Given
        SwapRequest request = SwapRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .from("0x1234567890123456789012345678901234567890")
                .origin("0x1234567890123456789012345678901234567890")
                .slippage(1.0)
                .build();

        SwapResponse expectedResponse = new SwapResponse();
        expectedResponse.setDstAmount("1000000000000000000");

        when(apiService.getSwap(any(), any(), any(), any(), 
                any(), any(), any(), any(), any(), any(), 
                any(), any(), any(), any(), any(), any(), 
                any(), any(), any(), any(), any(), 
                any(), any(), any()))
                .thenReturn(Single.just(expectedResponse));

        // When
        SwapResponse result = swapService.getSwapRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals("1000000000000000000", result.getDstAmount());
    }

    @Test
    void testGetSpenderRx_Success() {
        // Given
        SpenderResponse expectedResponse = new SpenderResponse();
        expectedResponse.setAddress("0x1111111254eeb25477b68fb85ed929f73a960582");

        when(apiService.getSpender()).thenReturn(Single.just(expectedResponse));

        // When
        SpenderResponse result = swapService.getSpenderRx().blockingGet();

        // Then
        assertNotNull(result);
        assertEquals("0x1111111254eeb25477b68fb85ed929f73a960582", result.getAddress());
        verify(apiService).getSpender();
    }

    @Test
    void testGetApproveTransactionRx_Success() {
        // Given
        ApproveTransactionRequest request = ApproveTransactionRequest.builder()
                .tokenAddress("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .build();

        ApproveCallDataResponse expectedResponse = new ApproveCallDataResponse();
        expectedResponse.setData("0x095ea7b3...");

        when(apiService.getApproveTransaction(anyString(), anyString()))
                .thenReturn(Single.just(expectedResponse));

        // When
        ApproveCallDataResponse result = swapService.getApproveTransactionRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals("0x095ea7b3...", result.getData());
        verify(apiService).getApproveTransaction("0x111111111117dc0aa78b770fa6a738034120c302", "10000000000000000");
    }

    @Test
    void testGetAllowanceRx_Success() {
        // Given
        AllowanceRequest request = AllowanceRequest.builder()
                .tokenAddress("0x111111111117dc0aa78b770fa6a738034120c302")
                .walletAddress("0x1234567890123456789012345678901234567890")
                .build();

        AllowanceResponse expectedResponse = new AllowanceResponse();
        expectedResponse.setAllowance("115792089237316195423570985008687907853269984665640564039457584007913129639935");

        when(apiService.getAllowance(anyString(), anyString()))
                .thenReturn(Single.just(expectedResponse));

        // When
        AllowanceResponse result = swapService.getAllowanceRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals("115792089237316195423570985008687907853269984665640564039457584007913129639935", result.getAllowance());
        verify(apiService).getAllowance("0x111111111117dc0aa78b770fa6a738034120c302", "0x1234567890123456789012345678901234567890");
    }

    @Test
    void testGetQuoteRx_Error() {
        // Given
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .build();

        RuntimeException apiError = new RuntimeException("API Error");
        when(apiService.getQuote(any(), any(), any(), any(), any(), any(), 
                any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Single.error(apiError));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            swapService.getQuoteRx(request).blockingGet();
        });
        
        // Check that the cause is our OneInchException
        assertNotNull(exception.getCause());
        assertInstanceOf(OneInchException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("Unexpected error"));
    }

    // Legacy method tests for backward compatibility
    @Test
    void testGetQuote_Success() throws OneInchException {
        // Given
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .build();

        QuoteResponse expectedResponse = new QuoteResponse();
        expectedResponse.setDstAmount("1000000000000000000");

        when(apiService.getQuote(any(), any(), any(), any(), any(), any(), 
                any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Single.just(expectedResponse));

        // When
        QuoteResponse result = swapService.getQuote(request);

        // Then
        assertNotNull(result);
        assertEquals("1000000000000000000", result.getDstAmount());
    }

    @Test
    void testGetQuoteAsync_Success() {
        // Given
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .build();

        QuoteResponse expectedResponse = new QuoteResponse();
        expectedResponse.setDstAmount("1000000000000000000");

        when(apiService.getQuote(any(), any(), any(), any(), any(), any(), 
                any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Single.just(expectedResponse));

        // When
        CompletableFuture<QuoteResponse> futureResult = swapService.getQuoteAsync(request);

        // Then
        assertNotNull(futureResult);
        QuoteResponse result = futureResult.join();
        assertEquals("1000000000000000000", result.getDstAmount());
    }

    @Test
    void testGetQuote_Exception() {
        // Given
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .build();

        when(apiService.getQuote(anyString(), anyString(), anyString(), any(), any(), any(), 
                any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Single.error(new RuntimeException("API Error")));

        // When & Then
        OneInchException exception = assertThrows(OneInchException.class, () -> {
            swapService.getQuote(request);
        });

        assertEquals("Quote request failed", exception.getMessage());
    }

    @Test
    void testGetQuoteAsync_Exception() {
        // Given
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .build();

        when(apiService.getQuote(anyString(), anyString(), anyString(), any(), any(), any(), 
                any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Single.error(new RuntimeException("API Error")));

        // When
        CompletableFuture<QuoteResponse> futureResult = swapService.getQuoteAsync(request);

        // Then
        assertThrows(CompletionException.class, futureResult::join);
    }
}