package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchFusionOrdersApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.Meta;
import io.oneinch.sdk.model.fusion.*;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FusionOrdersServiceImplTest {

    @Mock
    private OneInchFusionOrdersApiService apiService;

    private FusionOrdersServiceImpl fusionOrdersService;

    private static final Integer CHAIN_ID = 1; // Ethereum
    private static final String ORDER_HASH = "0x806039f5149065924ad52de616b50abff488c986716d052e9c160887bc09e559";
    private static final String MAKER_ADDRESS = "0x995BE1CA945174D5bA75410C1E658a41eB13a2FA";
    private static final String SETTLEMENT_ADDRESS = "0x1111111254EEB25477B68fb85Ed929f73A960582";

    @BeforeEach
    void setUp() {
        fusionOrdersService = new FusionOrdersServiceImpl(apiService);
    }

    // ==================== ACTIVE ORDERS TESTS ====================

    @Test
    void testGetActiveOrdersRx_Success() {
        // Given
        FusionActiveOrdersRequest request = FusionActiveOrdersRequest.builder()
                .chainId(CHAIN_ID)
                .page(1)
                .limit(10)
                .version("2.0")
                .build();

        ActiveOrdersOutput activeOrder = ActiveOrdersOutput.builder()
                .orderHash(ORDER_HASH)
                .signature("0x38de7c8c406c8668eec947d59679028c068735e56c8a41bcc5b3dc2d2229dec258424e0f06b189d2b87f9f3d9cdd9edcb7b3be4108bd8605d052c20c84e65ad61c")
                .deadline(OffsetDateTime.now().plusHours(1))
                .auctionStartDate(OffsetDateTime.now())
                .auctionEndDate(OffsetDateTime.now().plusMinutes(30))
                .quoteId("quote123")
                .remainingMakerAmount("1000000000000000000")
                .version("2.0")
                .build();

        GetActiveOrdersOutput expectedResponse = GetActiveOrdersOutput.builder()
                .meta(Meta.builder()
                        .totalItems(1L)
                        .itemsPerPage(10)
                        .totalPages(1)
                        .currentPage(1)
                        .build())
                .items(List.of(activeOrder))
                .build();

        when(apiService.getActiveOrders(CHAIN_ID, 1, 10, "2.0"))
                .thenReturn(Single.just(expectedResponse));

        // When
        GetActiveOrdersOutput result = fusionOrdersService.getActiveOrdersRx(request).blockingGet();

        // Then
        assertEquals(expectedResponse, result);
        assertEquals(1, result.getItems().size());
        assertEquals(ORDER_HASH, result.getItems().get(0).getOrderHash());
        verify(apiService).getActiveOrders(CHAIN_ID, 1, 10, "2.0");
    }

    @Test
    void testGetActiveOrdersRx_Error() {
        // Given
        FusionActiveOrdersRequest request = FusionActiveOrdersRequest.builder()
                .chainId(CHAIN_ID)
                .page(1)
                .limit(10)
                .build();

        RuntimeException exception = new RuntimeException("API Error");
        when(apiService.getActiveOrders(CHAIN_ID, 1, 10, null))
                .thenReturn(Single.error(exception));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                fusionOrdersService.getActiveOrdersRx(request).blockingGet());
    }

    @Test
    void testGetActiveOrdersAsync() throws Exception {
        // Given
        FusionActiveOrdersRequest request = FusionActiveOrdersRequest.builder()
                .chainId(CHAIN_ID)
                .page(1)
                .limit(5)
                .build();

        GetActiveOrdersOutput expectedResponse = GetActiveOrdersOutput.builder()
                .meta(Meta.builder().totalItems(0L).itemsPerPage(5).totalPages(0).currentPage(1).build())
                .items(List.of())
                .build();

        when(apiService.getActiveOrders(CHAIN_ID, 1, 5, null))
                .thenReturn(Single.just(expectedResponse));

        // When
        CompletableFuture<GetActiveOrdersOutput> future = fusionOrdersService.getActiveOrdersAsync(request);
        GetActiveOrdersOutput result = future.get();

        // Then
        assertEquals(expectedResponse, result);
        assertEquals(0, result.getItems().size());
    }

    @Test
    void testGetActiveOrders_Synchronous() throws OneInchException {
        // Given
        FusionActiveOrdersRequest request = FusionActiveOrdersRequest.builder()
                .chainId(CHAIN_ID)
                .page(1)
                .limit(10)
                .build();

        GetActiveOrdersOutput expectedResponse = GetActiveOrdersOutput.builder()
                .meta(Meta.builder().totalItems(0L).itemsPerPage(10).totalPages(0).currentPage(1).build())
                .items(List.of())
                .build();

        when(apiService.getActiveOrders(CHAIN_ID, 1, 10, null))
                .thenReturn(Single.just(expectedResponse));

        // When
        GetActiveOrdersOutput result = fusionOrdersService.getActiveOrders(request);

        // Then
        assertEquals(expectedResponse, result);
    }

    // ==================== SETTLEMENT CONTRACT TESTS ====================

    @Test
    void testGetSettlementContractRx_Success() {
        // Given
        SettlementAddressOutput expectedResponse = SettlementAddressOutput.builder()
                .address(SETTLEMENT_ADDRESS)
                .build();

        when(apiService.getSettlementContract(CHAIN_ID))
                .thenReturn(Single.just(expectedResponse));

        // When
        SettlementAddressOutput result = fusionOrdersService.getSettlementContractRx(CHAIN_ID).blockingGet();

        // Then
        assertEquals(expectedResponse, result);
        assertEquals(SETTLEMENT_ADDRESS, result.getAddress());
        verify(apiService).getSettlementContract(CHAIN_ID);
    }

    @Test
    void testGetSettlementContractAsync() throws Exception {
        // Given
        SettlementAddressOutput expectedResponse = SettlementAddressOutput.builder()
                .address(SETTLEMENT_ADDRESS)
                .build();

        when(apiService.getSettlementContract(CHAIN_ID))
                .thenReturn(Single.just(expectedResponse));

        // When
        CompletableFuture<SettlementAddressOutput> future = fusionOrdersService.getSettlementContractAsync(CHAIN_ID);
        SettlementAddressOutput result = future.get();

        // Then
        assertEquals(expectedResponse, result);
    }

    @Test
    void testGetSettlementContract_Synchronous() throws OneInchException {
        // Given
        SettlementAddressOutput expectedResponse = SettlementAddressOutput.builder()
                .address(SETTLEMENT_ADDRESS)
                .build();

        when(apiService.getSettlementContract(CHAIN_ID))
                .thenReturn(Single.just(expectedResponse));

        // When
        SettlementAddressOutput result = fusionOrdersService.getSettlementContract(CHAIN_ID);

        // Then
        assertEquals(expectedResponse, result);
    }

    // ==================== ORDER STATUS TESTS ====================

    @Test
    void testGetOrderByOrderHashRx_Success() {
        // Given
        LimitOrderV4StructOutput order = LimitOrderV4StructOutput.builder()
                .salt("0x123")
                .maker(MAKER_ADDRESS)
                .receiver(MAKER_ADDRESS)
                .makerAsset("0xA0b86a33E6aB6b6ce4e5a5B7db2e8Df6b1D2b9C7")
                .takerAsset("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .makingAmount("1000000000000000000")
                .takingAmount("100000000000000000")
                .makerTraits("0x0")
                .build();

        GetOrderFillsByHashOutput expectedResponse = GetOrderFillsByHashOutput.builder()
                .orderHash(ORDER_HASH)
                .status(OrderStatus.PENDING)
                .order(order)
                .extension("0x")
                .approximateTakingAmount("100000000000000000")
                .fills(List.of())
                .auctionStartDate(System.currentTimeMillis())
                .auctionDuration(1800000L) // 30 minutes
                .initialRateBump(1000)
                .positiveSurplus("0")
                .isNativeCurrency(true)
                .version("2.0")
                .build();

        when(apiService.getOrderByOrderHash(CHAIN_ID, ORDER_HASH))
                .thenReturn(Single.just(expectedResponse));

        // When
        GetOrderFillsByHashOutput result = fusionOrdersService.getOrderByOrderHashRx(CHAIN_ID, ORDER_HASH).blockingGet();

        // Then
        assertEquals(expectedResponse, result);
        assertEquals(ORDER_HASH, result.getOrderHash());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(apiService).getOrderByOrderHash(CHAIN_ID, ORDER_HASH);
    }

    @Test
    void testGetOrderByOrderHashAsync() throws Exception {
        // Given
        GetOrderFillsByHashOutput expectedResponse = GetOrderFillsByHashOutput.builder()
                .orderHash(ORDER_HASH)
                .status(OrderStatus.FILLED)
                .build();

        when(apiService.getOrderByOrderHash(CHAIN_ID, ORDER_HASH))
                .thenReturn(Single.just(expectedResponse));

        // When
        CompletableFuture<GetOrderFillsByHashOutput> future = 
                fusionOrdersService.getOrderByOrderHashAsync(CHAIN_ID, ORDER_HASH);
        GetOrderFillsByHashOutput result = future.get();

        // Then
        assertEquals(expectedResponse, result);
    }

    @Test
    void testGetOrderByOrderHash_Synchronous() throws OneInchException {
        // Given
        GetOrderFillsByHashOutput expectedResponse = GetOrderFillsByHashOutput.builder()
                .orderHash(ORDER_HASH)
                .status(OrderStatus.FILLED)
                .build();

        when(apiService.getOrderByOrderHash(CHAIN_ID, ORDER_HASH))
                .thenReturn(Single.just(expectedResponse));

        // When
        GetOrderFillsByHashOutput result = fusionOrdersService.getOrderByOrderHash(CHAIN_ID, ORDER_HASH);

        // Then
        assertEquals(expectedResponse, result);
    }

    // ==================== BATCH ORDER STATUS TESTS ====================

    @Test
    void testGetOrdersByOrderHashesRx_Success() {
        // Given
        OrdersByHashesInput request = OrdersByHashesInput.builder()
                .orderHashes(Arrays.asList(ORDER_HASH, "0x20ea5bd12b2d04566e175de24c2df41a058bf16df4af3eb2fb9bff38a9da98e8"))
                .build();

        GetOrderFillsByHashOutput expectedResponse = GetOrderFillsByHashOutput.builder()
                .orderHash(ORDER_HASH)
                .status(OrderStatus.FILLED)
                .build();

        when(apiService.getOrdersByOrderHashes(CHAIN_ID, request))
                .thenReturn(Single.just(expectedResponse));

        // When
        GetOrderFillsByHashOutput result = fusionOrdersService.getOrdersByOrderHashesRx(CHAIN_ID, request).blockingGet();

        // Then
        assertEquals(expectedResponse, result);
        verify(apiService).getOrdersByOrderHashes(CHAIN_ID, request);
    }

    // ==================== ORDER HISTORY TESTS ====================

    @Test
    void testGetOrdersByMakerRx_Success() {
        // Given
        FusionOrderHistoryRequest request = FusionOrderHistoryRequest.builder()
                .chainId(CHAIN_ID)
                .address(MAKER_ADDRESS)
                .page(1)
                .limit(10)
                .build();

        OrderFillsByMakerOutput expectedResponse = OrderFillsByMakerOutput.builder()
                .receiver(MAKER_ADDRESS)
                .orderHash(ORDER_HASH)
                .status(OrderStatus.FILLED)
                .makerAsset("0xA0b86a33E6aB6b6ce4e5a5B7db2e8Df6b1D2b9C7")
                .makerAmount("1000000000000000000")
                .minTakerAmount("100000000000000000")
                .approximateTakingAmount("100000000000000000")
                .positiveSurplus("1000")
                .takerAsset("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")
                .fills(List.of())
                .points(List.of())
                .auctionStartDate(System.currentTimeMillis())
                .auctionDuration(1800000L)
                .initialRateBump(1000)
                .isNativeCurrency(true)
                .version("2.0")
                .makerTraits("0x0")
                .build();

        when(apiService.getOrdersByMaker(
                CHAIN_ID, MAKER_ADDRESS, 1, 10, null, null, null, null, null, null))
                .thenReturn(Single.just(expectedResponse));

        // When
        OrderFillsByMakerOutput result = fusionOrdersService.getOrdersByMakerRx(request).blockingGet();

        // Then
        assertEquals(expectedResponse, result);
        assertEquals(MAKER_ADDRESS, result.getReceiver());
        assertEquals(OrderStatus.FILLED, result.getStatus());
        verify(apiService).getOrdersByMaker(
                CHAIN_ID, MAKER_ADDRESS, 1, 10, null, null, null, null, null, null);
    }

    @Test
    void testGetOrdersByMakerAsync() throws Exception {
        // Given
        FusionOrderHistoryRequest request = FusionOrderHistoryRequest.builder()
                .chainId(CHAIN_ID)
                .address(MAKER_ADDRESS)
                .page(1)
                .limit(5)
                .build();

        OrderFillsByMakerOutput expectedResponse = OrderFillsByMakerOutput.builder()
                .receiver(MAKER_ADDRESS)
                .orderHash(ORDER_HASH)
                .status(OrderStatus.PENDING)
                .build();

        when(apiService.getOrdersByMaker(
                CHAIN_ID, MAKER_ADDRESS, 1, 5, null, null, null, null, null, null))
                .thenReturn(Single.just(expectedResponse));

        // When
        CompletableFuture<OrderFillsByMakerOutput> future = fusionOrdersService.getOrdersByMakerAsync(request);
        OrderFillsByMakerOutput result = future.get();

        // Then
        assertEquals(expectedResponse, result);
    }

    @Test
    void testGetOrdersByMaker_Synchronous() throws OneInchException {
        // Given
        FusionOrderHistoryRequest request = FusionOrderHistoryRequest.builder()
                .chainId(CHAIN_ID)
                .address(MAKER_ADDRESS)
                .page(1)
                .limit(10)
                .build();

        OrderFillsByMakerOutput expectedResponse = OrderFillsByMakerOutput.builder()
                .receiver(MAKER_ADDRESS)
                .orderHash(ORDER_HASH)
                .status(OrderStatus.FILLED)
                .build();

        when(apiService.getOrdersByMaker(
                CHAIN_ID, MAKER_ADDRESS, 1, 10, null, null, null, null, null, null))
                .thenReturn(Single.just(expectedResponse));

        // When
        OrderFillsByMakerOutput result = fusionOrdersService.getOrdersByMaker(request);

        // Then
        assertEquals(expectedResponse, result);
    }
}