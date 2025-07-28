package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchFusionPlusOrdersApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.Meta;
import io.oneinch.sdk.model.fusionplus.*;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FusionPlusOrdersServiceImplTest {

    @Mock
    private OneInchFusionPlusOrdersApiService apiService;

    private FusionPlusOrdersServiceImpl fusionPlusOrdersService;

    private static final Integer ETHEREUM_CHAIN_ID = 1;
    private static final Integer POLYGON_CHAIN_ID = 137;
    private static final String WALLET_ADDRESS = "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8";
    private static final String ORDER_HASH = "0x806039f5149065924ad52de616b50abff488c986716d052e9c160887bc09e559";

    @BeforeEach
    void setUp() {
        fusionPlusOrdersService = new FusionPlusOrdersServiceImpl(apiService);
    }

    // Helper methods to create test data
    private FusionPlusActiveOrdersRequest createActiveOrdersRequest() {
        return FusionPlusActiveOrdersRequest.builder()
                .srcChain(ETHEREUM_CHAIN_ID)
                .dstChain(POLYGON_CHAIN_ID)
                .page(1)
                .limit(10)
                .sortBy("createdAt")
                .build();
    }

    private GetActiveOrdersOutput createActiveOrdersOutput() {
        CrossChainOrderDto order = CrossChainOrderDto.builder()
                .orderHash(ORDER_HASH)
                .srcChainId(ETHEREUM_CHAIN_ID)
                .dstChainId(POLYGON_CHAIN_ID)
                .status("active")
                .secretHashes(Arrays.asList("0x123", "0x456"))
                .srcEscrowFactory("0xfactory1")
                .dstEscrowFactory("0xfactory2")
                .srcSafetyDeposit("1000000000000000000")
                .dstSafetyDeposit("2000000000000000000")
                .build();

        return GetActiveOrdersOutput.builder()
                .items(Arrays.asList(order))
                .meta(Meta.builder().totalItems(1L).currentPage(1).build())
                .totalCrossChainOrders(1)
                .supportedSrcChains(Arrays.asList(1, 56, 137))
                .supportedDstChains(Arrays.asList(1, 56, 137, 42161))
                .build();
    }

    private CrossChainOrderDto createCrossChainOrder() {
        return CrossChainOrderDto.builder()
                .orderHash(ORDER_HASH)
                .srcChainId(ETHEREUM_CHAIN_ID)
                .dstChainId(POLYGON_CHAIN_ID)
                .status("active")
                .secretHashes(Arrays.asList("0x123", "0x456"))
                .srcEscrowFactory("0xfactory1")
                .dstEscrowFactory("0xfactory2")
                .srcSafetyDeposit("1000000000000000000")
                .dstSafetyDeposit("2000000000000000000")
                .createdAt(System.currentTimeMillis())
                .deadline(System.currentTimeMillis() + 3600000)
                .order(CrossChainOrderV4.builder()
                        .salt("0x123")
                        .maker(WALLET_ADDRESS)
                        .receiver(WALLET_ADDRESS)
                        .makerAsset("0xA0b86a33E6aB6b6ce4e5a5B7db2e8Df6b1D2b9C7")
                        .takerAsset("0x2791bca1f2de4661ed88a30c99a7a9449aa84174")
                        .makingAmount("1000000000000000000")
                        .takingAmount("1000000000")
                        .makerTraits("0x0")
                        .srcChainId(ETHEREUM_CHAIN_ID)
                        .dstChainId(POLYGON_CHAIN_ID)
                        .build())
                .build();
    }

    private PublicActionOutput createPublicActionOutput() {
        return PublicActionOutput.builder()
                .orderHash(ORDER_HASH)
                .actionType("withdrawal")
                .chainId(POLYGON_CHAIN_ID)
                .calldata("0xabcdef")
                .target("0x1234567890abcdef1234567890abcdef12345678")
                .value("0")
                .availableAt(System.currentTimeMillis() + 1800000)
                .available(false)
                .gasEstimate("21000")
                .build();
    }

    // ==================== ACTIVE CROSS-CHAIN ORDERS TESTS ====================

    @Test
    void testGetActiveOrdersRx_Success() {
        // Given
        FusionPlusActiveOrdersRequest request = createActiveOrdersRequest();
        GetActiveOrdersOutput expectedOutput = createActiveOrdersOutput();

        when(apiService.getActiveOrders(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, 1, 10, "createdAt"))
                .thenReturn(Single.just(expectedOutput));

        // When
        GetActiveOrdersOutput result = fusionPlusOrdersService.getActiveOrdersRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(1, result.getMeta().getTotalItems());
        assertEquals(1, result.getTotalCrossChainOrders());
        assertEquals(3, result.getSupportedSrcChains().size());
        assertEquals(4, result.getSupportedDstChains().size());
        assertEquals(ORDER_HASH, result.getItems().get(0).getOrderHash());
        verify(apiService).getActiveOrders(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, 1, 10, "createdAt");
    }

    @Test
    void testGetActiveOrdersAsync() throws Exception {
        // Given
        FusionPlusActiveOrdersRequest request = createActiveOrdersRequest();
        GetActiveOrdersOutput expectedOutput = createActiveOrdersOutput();

        when(apiService.getActiveOrders(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, 1, 10, "createdAt"))
                .thenReturn(Single.just(expectedOutput));

        // When
        CompletableFuture<GetActiveOrdersOutput> future = fusionPlusOrdersService.getActiveOrdersAsync(request);
        GetActiveOrdersOutput result = future.get();

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalCrossChainOrders());
        verify(apiService).getActiveOrders(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, 1, 10, "createdAt");
    }

    @Test
    void testGetActiveOrders_Synchronous() throws OneInchException {
        // Given
        FusionPlusActiveOrdersRequest request = createActiveOrdersRequest();
        GetActiveOrdersOutput expectedOutput = createActiveOrdersOutput();

        when(apiService.getActiveOrders(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, 1, 10, "createdAt"))
                .thenReturn(Single.just(expectedOutput));

        // When
        GetActiveOrdersOutput result = fusionPlusOrdersService.getActiveOrders(request);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalCrossChainOrders());
        verify(apiService).getActiveOrders(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, 1, 10, "createdAt");
    }

    @Test
    void testGetActiveOrdersRx_Error() {
        // Given
        FusionPlusActiveOrdersRequest request = createActiveOrdersRequest();
        RuntimeException exception = new RuntimeException("API Error");

        when(apiService.getActiveOrders(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, 1, 10, "createdAt"))
                .thenReturn(Single.error(exception));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                fusionPlusOrdersService.getActiveOrdersRx(request).blockingGet());
    }

    // ==================== CROSS-CHAIN ORDER STATUS TESTS ====================

    @Test
    void testGetOrderByOrderHashRx_Success() {
        // Given
        CrossChainOrderDto expectedOrder = createCrossChainOrder();

        when(apiService.getOrderByOrderHash(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH))
                .thenReturn(Single.just(expectedOrder));

        // When
        CrossChainOrderDto result = fusionPlusOrdersService.getOrderByOrderHashRx(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(ORDER_HASH, result.getOrderHash());
        assertEquals(ETHEREUM_CHAIN_ID, result.getSrcChainId());
        assertEquals(POLYGON_CHAIN_ID, result.getDstChainId());
        assertEquals("active", result.getStatus());
        assertEquals(2, result.getSecretHashes().size());
        assertNotNull(result.getOrder());
        assertEquals(WALLET_ADDRESS, result.getOrder().getMaker());
        verify(apiService).getOrderByOrderHash(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH);
    }

    @Test
    void testGetOrderByOrderHashAsync() throws Exception {
        // Given
        CrossChainOrderDto expectedOrder = createCrossChainOrder();

        when(apiService.getOrderByOrderHash(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH))
                .thenReturn(Single.just(expectedOrder));

        // When
        CompletableFuture<CrossChainOrderDto> future = fusionPlusOrdersService.getOrderByOrderHashAsync(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH);
        CrossChainOrderDto result = future.get();

        // Then
        assertNotNull(result);
        assertEquals(ORDER_HASH, result.getOrderHash());
        verify(apiService).getOrderByOrderHash(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH);
    }

    @Test
    void testGetOrderByOrderHash_Synchronous() throws OneInchException {
        // Given
        CrossChainOrderDto expectedOrder = createCrossChainOrder();

        when(apiService.getOrderByOrderHash(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH))
                .thenReturn(Single.just(expectedOrder));

        // When
        CrossChainOrderDto result = fusionPlusOrdersService.getOrderByOrderHash(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH);

        // Then
        assertNotNull(result);
        assertEquals(ORDER_HASH, result.getOrderHash());
        verify(apiService).getOrderByOrderHash(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH);
    }

    // ==================== BATCH ORDER STATUS TESTS ====================

    @Test
    void testGetOrdersByOrderHashesRx_Success() {
        // Given
        String orderHashes = ORDER_HASH + ",0x789";
        GetActiveOrdersOutput expectedOutput = createActiveOrdersOutput();

        when(apiService.getOrdersByOrderHashes(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, orderHashes))
                .thenReturn(Single.just(expectedOutput));

        // When
        GetActiveOrdersOutput result = fusionPlusOrdersService.getOrdersByOrderHashesRx(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, orderHashes).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        verify(apiService).getOrdersByOrderHashes(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, orderHashes);
    }

    // ==================== ORDER HISTORY TESTS ====================

    @Test
    void testGetOrdersByMakerRx_Success() {
        // Given
        GetActiveOrdersOutput expectedOutput = createActiveOrdersOutput();

        when(apiService.getOrdersByMaker(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, WALLET_ADDRESS, 1, 5))
                .thenReturn(Single.just(expectedOutput));

        // When
        GetActiveOrdersOutput result = fusionPlusOrdersService.getOrdersByMakerRx(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, WALLET_ADDRESS, 1, 5).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        verify(apiService).getOrdersByMaker(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, WALLET_ADDRESS, 1, 5);
    }

    // ==================== ESCROW EVENTS TESTS ====================

    @Test
    void testGetEscrowEventsRx_Success() {
        // Given
        GetActiveOrdersOutput expectedOutput = createActiveOrdersOutput();

        when(apiService.getEscrowEvents(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH, 1, 10))
                .thenReturn(Single.just(expectedOutput));

        // When
        GetActiveOrdersOutput result = fusionPlusOrdersService.getEscrowEventsRx(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH, 1, 10).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        verify(apiService).getEscrowEvents(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH, 1, 10);
    }

    // ==================== PUBLIC ACTIONS TESTS ====================

    @Test
    void testGetPublicActionsRx_Success() {
        // Given
        PublicActionOutput expectedAction = createPublicActionOutput();

        when(apiService.getPublicActions(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH))
                .thenReturn(Single.just(expectedAction));

        // When
        PublicActionOutput result = fusionPlusOrdersService.getPublicActionsRx(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(ORDER_HASH, result.getOrderHash());
        assertEquals("withdrawal", result.getActionType());
        assertEquals(POLYGON_CHAIN_ID, result.getChainId());
        assertEquals(false, result.getAvailable());
        verify(apiService).getPublicActions(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH);
    }

    @Test
    void testGetPublicActionsAsync() throws Exception {
        // Given
        PublicActionOutput expectedAction = createPublicActionOutput();

        when(apiService.getPublicActions(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH))
                .thenReturn(Single.just(expectedAction));

        // When
        CompletableFuture<PublicActionOutput> future = fusionPlusOrdersService.getPublicActionsAsync(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH);
        PublicActionOutput result = future.get();

        // Then
        assertNotNull(result);
        assertEquals("withdrawal", result.getActionType());
        verify(apiService).getPublicActions(ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ORDER_HASH);
    }

    // ==================== SUPPORTED CHAINS TESTS ====================

    @Test
    void testGetSupportedChainsRx_Success() {
        // Given
        GetActiveOrdersOutput expectedOutput = GetActiveOrdersOutput.builder()
                .supportedSrcChains(Arrays.asList(1, 56, 137, 42161, 43114))
                .supportedDstChains(Arrays.asList(1, 56, 137, 42161, 43114, 10))
                .build();

        when(apiService.getSupportedChains())
                .thenReturn(Single.just(expectedOutput));

        // When
        GetActiveOrdersOutput result = fusionPlusOrdersService.getSupportedChainsRx().blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(5, result.getSupportedSrcChains().size());
        assertEquals(6, result.getSupportedDstChains().size());
        assertTrue(result.getSupportedSrcChains().contains(1)); // Ethereum
        assertTrue(result.getSupportedSrcChains().contains(137)); // Polygon
        assertTrue(result.getSupportedDstChains().contains(42161)); // Arbitrum
        verify(apiService).getSupportedChains();
    }

    @Test
    void testGetSupportedChainsAsync() throws Exception {
        // Given
        GetActiveOrdersOutput expectedOutput = GetActiveOrdersOutput.builder()
                .supportedSrcChains(Arrays.asList(1, 56, 137))
                .supportedDstChains(Arrays.asList(1, 56, 137, 42161))
                .build();

        when(apiService.getSupportedChains())
                .thenReturn(Single.just(expectedOutput));

        // When
        CompletableFuture<GetActiveOrdersOutput> future = fusionPlusOrdersService.getSupportedChainsAsync();
        GetActiveOrdersOutput result = future.get();

        // Then
        assertNotNull(result);
        assertEquals(3, result.getSupportedSrcChains().size());
        assertEquals(4, result.getSupportedDstChains().size());
        verify(apiService).getSupportedChains();
    }

    @Test
    void testGetSupportedChains_Synchronous() throws OneInchException {
        // Given
        GetActiveOrdersOutput expectedOutput = GetActiveOrdersOutput.builder()
                .supportedSrcChains(Arrays.asList(1, 56, 137))
                .supportedDstChains(Arrays.asList(1, 56, 137, 42161))
                .build();

        when(apiService.getSupportedChains())
                .thenReturn(Single.just(expectedOutput));

        // When
        GetActiveOrdersOutput result = fusionPlusOrdersService.getSupportedChains();

        // Then
        assertNotNull(result);
        assertEquals(3, result.getSupportedSrcChains().size());
        verify(apiService).getSupportedChains();
    }

    @Test
    void testGetSupportedChainsRx_Error() {
        // Given
        RuntimeException exception = new RuntimeException("Network error");

        when(apiService.getSupportedChains())
                .thenReturn(Single.error(exception));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                fusionPlusOrdersService.getSupportedChainsRx().blockingGet());
    }
}