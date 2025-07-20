package io.oneinch.sdk.service;

import io.oneinch.sdk.client.HttpClient;
import io.oneinch.sdk.exception.OneInchApiException;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwapServiceImplTest {

    @Mock
    private HttpClient httpClient;

    private SwapServiceImpl swapService;

    @BeforeEach
    void setUp() {
        swapService = new SwapServiceImpl(httpClient);
    }

    @Test
    void testGetQuote_Success() throws OneInchException {
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .build();

        QuoteResponse expectedResponse = new QuoteResponse();
        expectedResponse.setDstAmount("1000000000000000000");

        when(httpClient.get(eq("/quote"), any(Map.class), eq(QuoteResponse.class)))
                .thenReturn(expectedResponse);

        QuoteResponse result = swapService.getQuote(request);

        assertNotNull(result);
        assertEquals("1000000000000000000", result.getDstAmount());
        verify(httpClient).get(eq("/quote"), any(Map.class), eq(QuoteResponse.class));
    }

    @Test
    void testGetQuote_WithAllParameters() throws OneInchException {
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

        when(httpClient.get(eq("/quote"), any(Map.class), eq(QuoteResponse.class)))
                .thenReturn(expectedResponse);

        QuoteResponse result = swapService.getQuote(request);

        assertNotNull(result);
        assertEquals("1000000000000000000", result.getDstAmount());
        verify(httpClient).get(eq("/quote"), any(Map.class), eq(QuoteResponse.class));
    }

    @Test
    void testGetQuoteAsync_Success() {
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .build();

        QuoteResponse expectedResponse = new QuoteResponse();
        expectedResponse.setDstAmount("1000000000000000000");

        when(httpClient.getAsync(eq("/quote"), any(Map.class), eq(QuoteResponse.class)))
                .thenReturn(CompletableFuture.completedFuture(expectedResponse));

        CompletableFuture<QuoteResponse> futureResult = swapService.getQuoteAsync(request);

        assertNotNull(futureResult);
        QuoteResponse result = futureResult.join();
        assertEquals("1000000000000000000", result.getDstAmount());
        verify(httpClient).getAsync(eq("/quote"), any(Map.class), eq(QuoteResponse.class));
    }

    @Test
    void testGetSwap_Success() throws OneInchException {
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

        when(httpClient.get(eq("/swap"), any(Map.class), eq(SwapResponse.class)))
                .thenReturn(expectedResponse);

        SwapResponse result = swapService.getSwap(request);

        assertNotNull(result);
        assertEquals("1000000000000000000", result.getDstAmount());
        verify(httpClient).get(eq("/swap"), any(Map.class), eq(SwapResponse.class));
    }

    @Test
    void testGetSpender_Success() throws OneInchException {
        SpenderResponse expectedResponse = new SpenderResponse();
        expectedResponse.setAddress("0x1111111254eeb25477b68fb85ed929f73a960582");

        when(httpClient.get(eq("/approve/spender"), isNull(), eq(SpenderResponse.class)))
                .thenReturn(expectedResponse);

        SpenderResponse result = swapService.getSpender();

        assertNotNull(result);
        assertEquals("0x1111111254eeb25477b68fb85ed929f73a960582", result.getAddress());
        verify(httpClient).get(eq("/approve/spender"), isNull(), eq(SpenderResponse.class));
    }

    @Test
    void testGetApproveTransaction_Success() throws OneInchException {
        ApproveTransactionRequest request = ApproveTransactionRequest.builder()
                .tokenAddress("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .build();

        ApproveCallDataResponse expectedResponse = new ApproveCallDataResponse();
        expectedResponse.setData("0x095ea7b3...");

        when(httpClient.get(eq("/approve/transaction"), any(Map.class), eq(ApproveCallDataResponse.class)))
                .thenReturn(expectedResponse);

        ApproveCallDataResponse result = swapService.getApproveTransaction(request);

        assertNotNull(result);
        assertEquals("0x095ea7b3...", result.getData());
        verify(httpClient).get(eq("/approve/transaction"), any(Map.class), eq(ApproveCallDataResponse.class));
    }

    @Test
    void testGetAllowance_Success() throws OneInchException {
        AllowanceRequest request = AllowanceRequest.builder()
                .tokenAddress("0x111111111117dc0aa78b770fa6a738034120c302")
                .walletAddress("0x1234567890123456789012345678901234567890")
                .build();

        AllowanceResponse expectedResponse = new AllowanceResponse();
        expectedResponse.setAllowance("115792089237316195423570985008687907853269984665640564039457584007913129639935");

        when(httpClient.get(eq("/approve/allowance"), any(Map.class), eq(AllowanceResponse.class)))
                .thenReturn(expectedResponse);

        AllowanceResponse result = swapService.getAllowance(request);

        assertNotNull(result);
        assertEquals("115792089237316195423570985008687907853269984665640564039457584007913129639935", result.getAllowance());
        verify(httpClient).get(eq("/approve/allowance"), any(Map.class), eq(AllowanceResponse.class));
    }

    @Test
    void testGetQuote_ApiException() throws OneInchException {
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .build();

        OneInchApiException apiException = new OneInchApiException("Bad Request", "insufficient liquidity", 400, "request-id", null);

        when(httpClient.get(eq("/quote"), any(Map.class), eq(QuoteResponse.class)))
                .thenThrow(apiException);

        OneInchApiException exception = assertThrows(OneInchApiException.class, () -> {
            swapService.getQuote(request);
        });

        assertEquals("Bad Request", exception.getError());
        assertEquals("insufficient liquidity", exception.getDescription());
        assertEquals(400, exception.getStatusCode());
        assertEquals("request-id", exception.getRequestId());
    }

    @Test
    void testGetQuoteAsync_Exception() {
        QuoteRequest request = QuoteRequest.builder()
                .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .dst("0x111111111117dc0aa78b770fa6a738034120c302")
                .amount("10000000000000000")
                .build();

        OneInchApiException apiException = new OneInchApiException("Bad Request", "insufficient liquidity", 400, "request-id", null);

        when(httpClient.getAsync(eq("/quote"), any(Map.class), eq(QuoteResponse.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException(apiException)));

        CompletableFuture<QuoteResponse> futureResult = swapService.getQuoteAsync(request);

        assertThrows(CompletionException.class, futureResult::join);
    }
}