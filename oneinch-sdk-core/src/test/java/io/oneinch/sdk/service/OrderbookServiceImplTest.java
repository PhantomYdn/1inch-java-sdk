package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchOrderbookApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.Meta;
import io.oneinch.sdk.model.orderbook.*;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderbookServiceImplTest {

    @Mock
    private OneInchOrderbookApiService apiService;

    private OrderbookServiceImpl orderbookService;

    @BeforeEach
    void setUp() {
        orderbookService = new OrderbookServiceImpl(apiService);
    }

    @Test
    void testCreateLimitOrderRx_Success() {
        // Given
        LimitOrderV4Data orderData = LimitOrderV4Data.builder()
                .makerAsset("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .takerAsset("0x111111111117dc0aa78b770fa6a738034120c302")
                .maker("0x1234567890123456789012345678901234567890")
                .makingAmount("1000000000000000000")
                .takingAmount("500000000")
                .salt("123456789")
                .build();

        LimitOrderV4Request request = LimitOrderV4Request.builder()
                .chainId(1)
                .orderHash("0xabcdef123456")
                .signature("0x1b123456")
                .data(orderData)
                .build();

        LimitOrderV4Response expectedResponse = new LimitOrderV4Response();
        expectedResponse.setSuccess(true);

        when(apiService.createLimitOrder(any(), any())).thenReturn(Single.just(expectedResponse));

        // When
        LimitOrderV4Response result = orderbookService.createLimitOrderRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(apiService).createLimitOrder(eq(1), eq(request));
    }

    @Test
    void testGetLimitOrdersByAddressRx_Success() {
        // Given
        GetLimitOrdersRequest request = GetLimitOrdersRequest.builder()
                .chainId(1)
                .address("0x1234567890123456789012345678901234567890")
                .page(1)
                .limit(10)
                .build();

        List<GetLimitOrdersV4Response> expectedOrders = List.of(new GetLimitOrdersV4Response());

        when(apiService.getLimitOrdersByAddress(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Single.just(expectedOrders));

        // When
        List<GetLimitOrdersV4Response> result = orderbookService.getLimitOrdersByAddressRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(apiService).getLimitOrdersByAddress(eq(1), eq("0x1234567890123456789012345678901234567890"),
                eq(1), eq(10), eq(null), eq(null), eq(null), eq(null));
    }

    @Test
    void testGetOrderByOrderHashRx_Success() {
        // Given
        Integer chainId = 1;
        String orderHash = "0xabcdef123456";
        GetLimitOrdersV4Response expectedOrder = new GetLimitOrdersV4Response();
        expectedOrder.setOrderHash(orderHash);

        when(apiService.getOrderByOrderHash(any(), any())).thenReturn(Single.just(expectedOrder));

        // When
        GetLimitOrdersV4Response result = orderbookService.getOrderByOrderHashRx(chainId, orderHash).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(orderHash, result.getOrderHash());
        verify(apiService).getOrderByOrderHash(eq(1), eq(orderHash));
    }

    @Test
    void testGetAllLimitOrdersRx_Success() {
        // Given
        GetAllLimitOrdersRequest request = GetAllLimitOrdersRequest.builder()
                .chainId(1)
                .page(1)
                .limit(50)
                .statuses("1,2")
                .build();

        List<GetLimitOrdersV4Response> expectedOrders = List.of(new GetLimitOrdersV4Response(), new GetLimitOrdersV4Response());

        when(apiService.getAllLimitOrders(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Single.just(expectedOrders));

        // When
        List<GetLimitOrdersV4Response> result = orderbookService.getAllLimitOrdersRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(apiService).getAllLimitOrders(eq(1), eq(1), eq(50), eq("1,2"), eq(null), eq(null), eq(null));
    }

    @Test
    void testGetOrdersCountRx_Success() {
        // Given
        GetLimitOrdersCountRequest request = GetLimitOrdersCountRequest.builder()
                .chainId(1)
                .statuses("1,2,3")
                .build();

        GetLimitOrdersCountV4Response expectedResponse = new GetLimitOrdersCountV4Response();
        expectedResponse.setCount(125L);

        when(apiService.getOrdersCount(any(), any(), any(), any())).thenReturn(Single.just(expectedResponse));

        // When
        GetLimitOrdersCountV4Response result = orderbookService.getOrdersCountRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(125L, result.getCount());
        verify(apiService).getOrdersCount(eq(1), eq("1,2,3"), eq(null), eq(null));
    }

    @Test
    void testGetEventsByOrderHashRx_Success() {
        // Given
        Integer chainId = 1;
        String orderHash = "0xabcdef123456";
        Map<String, List<GetEventsV4Response>> expectedEvents = Map.of(
                orderHash, List.of(new GetEventsV4Response())
        );

        when(apiService.getEventsByOrderHash(any(), any())).thenReturn(Single.just(expectedEvents));

        // When
        Map<String, List<GetEventsV4Response>> result = orderbookService.getEventsByOrderHashRx(chainId, orderHash).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(orderHash));
        verify(apiService).getEventsByOrderHash(eq(1), eq(orderHash));
    }

    @Test
    void testGetAllEventsRx_Success() {
        // Given
        GetEventsRequest request = GetEventsRequest.builder()
                .chainId(1)
                .limit(100)
                .build();

        List<GetEventsV4Response> expectedEvents = List.of(new GetEventsV4Response());

        when(apiService.getAllEvents(any(), any())).thenReturn(Single.just(expectedEvents));

        // When
        List<GetEventsV4Response> result = orderbookService.getAllEventsRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(apiService).getAllEvents(eq(1), eq(100));
    }

    @Test
    void testHasActiveOrdersWithPermitRx_Success() {
        // Given
        HasActiveOrdersWithPermitRequest request = HasActiveOrdersWithPermitRequest.builder()
                .chainId(1)
                .walletAddress("0x1234567890123456789012345678901234567890")
                .token("0x111111111117dc0aa78b770fa6a738034120c302")
                .build();

        GetHasActiveOrdersWithPermitV4Response expectedResponse = new GetHasActiveOrdersWithPermitV4Response();
        expectedResponse.setResult(true);

        when(apiService.hasActiveOrdersWithPermit(any(), any(), any())).thenReturn(Single.just(expectedResponse));

        // When
        GetHasActiveOrdersWithPermitV4Response result = orderbookService.hasActiveOrdersWithPermitRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertTrue(result.isResult());
        verify(apiService).hasActiveOrdersWithPermit(eq(1), 
                eq("0x1234567890123456789012345678901234567890"), 
                eq("0x111111111117dc0aa78b770fa6a738034120c302"));
    }

    @Test
    void testGetUniqueActivePairsRx_Success() {
        // Given
        GetUniqueActivePairsRequest request = GetUniqueActivePairsRequest.builder()
                .chainId(1)
                .page(1)
                .limit(10)
                .build();

        UniquePairs pair = new UniquePairs();
        pair.setMakerAsset("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        pair.setTakerAsset("0x111111111117dc0aa78b770fa6a738034120c302");

        Meta meta = new Meta();
        meta.setTotalItems(1L);
        meta.setItemsPerPage(10);
        meta.setCurrentPage(1);

        GetActiveUniquePairsResponse expectedResponse = new GetActiveUniquePairsResponse();
        expectedResponse.setMeta(meta);
        expectedResponse.setItems(List.of(pair));

        when(apiService.getUniqueActivePairs(any(), any(), any())).thenReturn(Single.just(expectedResponse));

        // When
        GetActiveUniquePairsResponse result = orderbookService.getUniqueActivePairsRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertNotNull(result.getMeta());
        assertEquals(1, result.getItems().size());
        assertEquals("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", result.getItems().get(0).getMakerAsset());
        verify(apiService).getUniqueActivePairs(eq(1), eq(1), eq(10));
    }

    // Error handling tests
    @Test
    void testCreateLimitOrderRx_Error() {
        // Given
        LimitOrderV4Data orderData = LimitOrderV4Data.builder()
                .makerAsset("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .takerAsset("0x111111111117dc0aa78b770fa6a738034120c302")
                .maker("0x1234567890123456789012345678901234567890")
                .makingAmount("1000000000000000000")
                .takingAmount("500000000")
                .salt("123456789")
                .build();

        LimitOrderV4Request request = LimitOrderV4Request.builder()
                .chainId(1)
                .orderHash("0xabcdef123456")
                .signature("0x1b123456")
                .data(orderData)
                .build();

        RuntimeException apiError = new RuntimeException("API Error");
        when(apiService.createLimitOrder(any(), any())).thenReturn(Single.error(apiError));

        // When & Then
        assertThrows(RuntimeException.class, () -> orderbookService.createLimitOrderRx(request).blockingGet());
        verify(apiService).createLimitOrder(any(), any());
    }

    // Synchronous method tests
    @Test
    void testCreateLimitOrder_Success() throws OneInchException {
        // Given
        LimitOrderV4Data orderData = LimitOrderV4Data.builder()
                .makerAsset("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .takerAsset("0x111111111117dc0aa78b770fa6a738034120c302")
                .maker("0x1234567890123456789012345678901234567890")
                .makingAmount("1000000000000000000")
                .takingAmount("500000000")
                .salt("123456789")
                .build();

        LimitOrderV4Request request = LimitOrderV4Request.builder()
                .chainId(1)
                .orderHash("0xabcdef123456")
                .signature("0x1b123456")
                .data(orderData)
                .build();

        LimitOrderV4Response expectedResponse = new LimitOrderV4Response();
        expectedResponse.setSuccess(true);

        when(apiService.createLimitOrder(any(), any())).thenReturn(Single.just(expectedResponse));

        // When
        LimitOrderV4Response result = orderbookService.createLimitOrder(request);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    // Async method tests
    @Test
    void testCreateLimitOrderAsync_Success() throws Exception {
        // Given
        LimitOrderV4Data orderData = LimitOrderV4Data.builder()
                .makerAsset("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .takerAsset("0x111111111117dc0aa78b770fa6a738034120c302")
                .maker("0x1234567890123456789012345678901234567890")
                .makingAmount("1000000000000000000")
                .takingAmount("500000000")
                .salt("123456789")
                .build();

        LimitOrderV4Request request = LimitOrderV4Request.builder()
                .chainId(1)
                .orderHash("0xabcdef123456")
                .signature("0x1b123456")
                .data(orderData)
                .build();

        LimitOrderV4Response expectedResponse = new LimitOrderV4Response();
        expectedResponse.setSuccess(true);

        when(apiService.createLimitOrder(any(), any())).thenReturn(Single.just(expectedResponse));

        // When
        CompletableFuture<LimitOrderV4Response> future = orderbookService.createLimitOrderAsync(request);
        LimitOrderV4Response result = future.get();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    void testCreateLimitOrder_Exception() {
        // Given
        LimitOrderV4Data orderData = LimitOrderV4Data.builder()
                .makerAsset("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .takerAsset("0x111111111117dc0aa78b770fa6a738034120c302")
                .maker("0x1234567890123456789012345678901234567890")
                .makingAmount("1000000000000000000")
                .takingAmount("500000000")
                .salt("123456789")
                .build();

        LimitOrderV4Request request = LimitOrderV4Request.builder()
                .chainId(1)
                .orderHash("0xabcdef123456")
                .signature("0x1b123456")
                .data(orderData)
                .build();

        when(apiService.createLimitOrder(any(), any())).thenReturn(Single.error(new RuntimeException("API Error")));

        // When & Then
        assertThrows(OneInchException.class, () -> orderbookService.createLimitOrder(request));
    }

    @Test
    void testCreateLimitOrderAsync_Exception() {
        // Given
        LimitOrderV4Data orderData = LimitOrderV4Data.builder()
                .makerAsset("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .takerAsset("0x111111111117dc0aa78b770fa6a738034120c302")
                .maker("0x1234567890123456789012345678901234567890")
                .makingAmount("1000000000000000000")
                .takingAmount("500000000")
                .salt("123456789")
                .build();

        LimitOrderV4Request request = LimitOrderV4Request.builder()
                .chainId(1)
                .orderHash("0xabcdef123456")
                .signature("0x1b123456")
                .data(orderData)
                .build();

        when(apiService.createLimitOrder(any(), any())).thenReturn(Single.error(new RuntimeException("API Error")));

        // When
        CompletableFuture<LimitOrderV4Response> future = orderbookService.createLimitOrderAsync(request);

        // Then
        assertThrows(CompletionException.class, future::join);
    }
}