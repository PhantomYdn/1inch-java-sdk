package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchFusionPlusQuoterApiService;
import io.oneinch.sdk.exception.OneInchException;
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
class FusionPlusQuoterServiceImplTest {

    @Mock
    private OneInchFusionPlusQuoterApiService apiService;

    private FusionPlusQuoterServiceImpl fusionPlusQuoterService;

    private static final Integer ETHEREUM_CHAIN_ID = 1;
    private static final Integer POLYGON_CHAIN_ID = 137;
    private static final String ETH_ADDRESS = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
    private static final String USDC_POLYGON = "0x2791bca1f2de4661ed88a30c99a7a9449aa84174";
    private static final String WALLET_ADDRESS = "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8";
    private static final String SWAP_AMOUNT = "1000000000000000000"; // 1 ETH

    @BeforeEach
    void setUp() {
        fusionPlusQuoterService = new FusionPlusQuoterServiceImpl(apiService);
    }

    // Helper methods to create test data
    private FusionPlusQuoteRequest createQuoteRequest() {
        return FusionPlusQuoteRequest.builder()
                .srcChain(ETHEREUM_CHAIN_ID)
                .dstChain(POLYGON_CHAIN_ID)
                .srcTokenAddress(ETH_ADDRESS)
                .dstTokenAddress(USDC_POLYGON)
                .amount(SWAP_AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .enableEstimate(true)
                .fee(100) // 1% fee
                .build();
    }

    private GetQuoteOutput createQuoteOutput() {
        TimeLocks timeLocks = TimeLocks.builder()
                .srcWithdrawal(1800)
                .srcPublicWithdrawal(3600)
                .srcCancellation(7200)
                .srcPublicCancellation(14400)
                .dstWithdrawal(1800)
                .dstPublicWithdrawal(3600)
                .dstCancellation(7200)
                .build();

        GasCostConfig gasCostConfig = GasCostConfig.builder()
                .gasBumpEstimate(50)
                .gasPriceEstimate("20000000000") // 20 gwei
                .build();

        AuctionPoint point1 = AuctionPoint.builder().delay(0).coefficient(1.0).build();
        AuctionPoint point2 = AuctionPoint.builder().delay(600).coefficient(0.8).build();
        AuctionPoint point3 = AuctionPoint.builder().delay(1200).coefficient(0.6).build();

        Preset fastPreset = Preset.builder()
                .auctionDuration(1200)
                .startAuctionIn(30)
                .initialRateBump(1000)
                .auctionStartAmount("2500000000")
                .startAmount("2500000000")
                .auctionEndAmount("2400000000")
                .exclusiveResolver(null)
                .costInDstToken("1000000")
                .points(Arrays.asList(point1, point2, point3))
                .allowPartialFills(false)
                .allowMultipleFills(false)
                .gasCost(gasCostConfig)
                .secretsCount(1)
                .build();

        Preset mediumPreset = Preset.builder()
                .auctionDuration(1800)
                .startAuctionIn(60)
                .initialRateBump(800)
                .auctionStartAmount("2550000000")
                .startAmount("2550000000")
                .auctionEndAmount("2450000000")
                .exclusiveResolver(null)
                .costInDstToken("800000")
                .points(Arrays.asList(point1, point2, point3))
                .allowPartialFills(true)
                .allowMultipleFills(false)
                .gasCost(gasCostConfig)
                .secretsCount(2)
                .build();

        QuotePresets presets = QuotePresets.builder()
                .fast(fastPreset)
                .medium(mediumPreset)
                .slow(mediumPreset) // Reuse for simplicity
                .build();

        TokenPair usdPair = TokenPair.builder()
                .srcToken("2500.00")
                .dstToken("1.001")
                .build();

        PairCurrency prices = PairCurrency.builder()
                .usd(usdPair)
                .build();

        return GetQuoteOutput.builder()
                .quoteId("fusion_plus_quote_12345")
                .srcTokenAmount(SWAP_AMOUNT)
                .dstTokenAmount("2500000000") // 2500 USDC
                .presets(presets)
                .srcEscrowFactory("0x1111111111111111111111111111111111111111")
                .dstEscrowFactory("0x2222222222222222222222222222222222222222")
                .whitelist(Arrays.asList("0x3333333333333333333333333333333333333333"))
                .timeLocks(timeLocks)
                .srcSafetyDeposit("100000000000000000") // 0.1 ETH
                .dstSafetyDeposit("250000000") // 250 USDC
                .recommendedPreset("fast")
                .prices(prices)
                .volume(prices) // Reuse for simplicity
                .build();
    }

    private CustomPresetParams createCustomPresetParams() {
        return CustomPresetParams.builder()
                .auctionDuration(2400) // 40 minutes
                .startAuctionIn(120) // 2 minutes
                .initialRateBump(1200)
                .auctionStartAmount("2600000000")
                .auctionEndAmount("2400000000")
                .points(Arrays.asList(
                        AuctionPoint.builder().delay(0).coefficient(1.0).build(),
                        AuctionPoint.builder().delay(1200).coefficient(0.7).build(),
                        AuctionPoint.builder().delay(2400).coefficient(0.5).build()
                ))
                .allowPartialFills(false)
                .allowMultipleFills(true)
                .secretsCount(2)
                .gasBumpEstimate(75)
                .gasPriceEstimate("25000000000") // 25 gwei
                .build();
    }

    private FusionPlusBuildOrderRequest createBuildOrderRequest() {
        return FusionPlusBuildOrderRequest.builder()
                .srcChain(ETHEREUM_CHAIN_ID)
                .dstChain(POLYGON_CHAIN_ID)
                .srcTokenAddress(ETH_ADDRESS)
                .dstTokenAddress(USDC_POLYGON)
                .amount(SWAP_AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .fee(100)
                .source("SDK-Test")
                .preset("fast")
                .build();
    }

    private BuildOrderBody createBuildOrderBody() {
        return BuildOrderBody.builder()
                .quote(createQuoteOutput())
                .secretsHashList("0x315b47a8c3780434b153667588db4ca628526e20000000000000000000000000")
                .build();
    }

    private BuildOrderOutput createBuildOrderOutput() {
        return BuildOrderOutput.builder()
                .typedData(new Object()) // Mock EIP712 typed data
                .orderHash("0x806039f5149065924ad52de616b50abff488c986716d052e9c160887bc09e559")
                .extension("0x1234567890abcdef1234567890abcdef12345678")
                .build();
    }

    // ==================== CROSS-CHAIN QUOTE GENERATION TESTS ====================

    @Test
    void testGetQuoteRx_Success() {
        // Given
        FusionPlusQuoteRequest request = createQuoteRequest();
        GetQuoteOutput expectedOutput = createQuoteOutput();

        when(apiService.getQuote(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null))
                .thenReturn(Single.just(expectedOutput));

        // When
        GetQuoteOutput result = fusionPlusQuoterService.getQuoteRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals("fusion_plus_quote_12345", result.getQuoteId());
        assertEquals(SWAP_AMOUNT, result.getSrcTokenAmount());
        assertEquals("2500000000", result.getDstTokenAmount());
        assertEquals("fast", result.getRecommendedPreset());
        assertEquals("0x1111111111111111111111111111111111111111", result.getSrcEscrowFactory());
        assertEquals("0x2222222222222222222222222222222222222222", result.getDstEscrowFactory());
        assertEquals("100000000000000000", result.getSrcSafetyDeposit());
        assertEquals("250000000", result.getDstSafetyDeposit());
        assertNotNull(result.getTimeLocks());
        assertEquals(1800, result.getTimeLocks().getSrcWithdrawal());
        assertEquals(1800, result.getTimeLocks().getDstWithdrawal());
        assertNotNull(result.getPresets());
        assertNotNull(result.getPresets().getFast());
        assertEquals(1200, result.getPresets().getFast().getAuctionDuration());
        assertEquals(1, result.getPresets().getFast().getSecretsCount());
        assertEquals(2, result.getPresets().getMedium().getSecretsCount());
        verify(apiService).getQuote(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null);
    }

    @Test
    void testGetQuoteAsync() throws Exception {
        // Given
        FusionPlusQuoteRequest request = createQuoteRequest();
        GetQuoteOutput expectedOutput = createQuoteOutput();

        when(apiService.getQuote(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null))
                .thenReturn(Single.just(expectedOutput));

        // When
        CompletableFuture<GetQuoteOutput> future = fusionPlusQuoterService.getQuoteAsync(request);
        GetQuoteOutput result = future.get();

        // Then
        assertNotNull(result);
        assertEquals("fusion_plus_quote_12345", result.getQuoteId());
        assertEquals("2500000000", result.getDstTokenAmount());
        verify(apiService).getQuote(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null);
    }

    @Test
    void testGetQuote_Synchronous() throws OneInchException {
        // Given
        FusionPlusQuoteRequest request = createQuoteRequest();
        GetQuoteOutput expectedOutput = createQuoteOutput();

        when(apiService.getQuote(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null))
                .thenReturn(Single.just(expectedOutput));

        // When
        GetQuoteOutput result = fusionPlusQuoterService.getQuote(request);

        // Then
        assertNotNull(result);
        assertEquals("fusion_plus_quote_12345", result.getQuoteId());
        verify(apiService).getQuote(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null);
    }

    @Test
    void testGetQuoteRx_Error() {
        // Given
        FusionPlusQuoteRequest request = createQuoteRequest();
        RuntimeException exception = new RuntimeException("Invalid token pair");

        when(apiService.getQuote(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null))
                .thenReturn(Single.error(exception));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                fusionPlusQuoterService.getQuoteRx(request).blockingGet());
    }

    // ==================== CUSTOM PRESET QUOTES TESTS ====================

    @Test
    void testGetQuoteWithCustomPresetsRx_Success() {
        // Given
        FusionPlusQuoteRequest request = createQuoteRequest();
        CustomPresetParams customPreset = createCustomPresetParams();
        GetQuoteOutput expectedOutput = createQuoteOutput();

        when(apiService.getQuoteWithCustomPresets(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null, customPreset))
                .thenReturn(Single.just(expectedOutput));

        // When
        GetQuoteOutput result = fusionPlusQuoterService.getQuoteWithCustomPresetsRx(request, customPreset).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals("fusion_plus_quote_12345", result.getQuoteId());
        assertEquals("fast", result.getRecommendedPreset());
        assertNotNull(result.getPresets());
        verify(apiService).getQuoteWithCustomPresets(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null, customPreset);
    }

    @Test
    void testGetQuoteWithCustomPresetsAsync() throws Exception {
        // Given
        FusionPlusQuoteRequest request = createQuoteRequest();
        CustomPresetParams customPreset = createCustomPresetParams();
        GetQuoteOutput expectedOutput = createQuoteOutput();

        when(apiService.getQuoteWithCustomPresets(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null, customPreset))
                .thenReturn(Single.just(expectedOutput));

        // When
        CompletableFuture<GetQuoteOutput> future = fusionPlusQuoterService.getQuoteWithCustomPresetsAsync(request, customPreset);
        GetQuoteOutput result = future.get();

        // Then
        assertNotNull(result);
        assertEquals("fusion_plus_quote_12345", result.getQuoteId());
        verify(apiService).getQuoteWithCustomPresets(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null, customPreset);
    }

    @Test
    void testGetQuoteWithCustomPresets_Synchronous() throws OneInchException {
        // Given
        FusionPlusQuoteRequest request = createQuoteRequest();
        CustomPresetParams customPreset = createCustomPresetParams();
        GetQuoteOutput expectedOutput = createQuoteOutput();

        when(apiService.getQuoteWithCustomPresets(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null, customPreset))
                .thenReturn(Single.just(expectedOutput));

        // When
        GetQuoteOutput result = fusionPlusQuoterService.getQuoteWithCustomPresets(request, customPreset);

        // Then
        assertNotNull(result);
        assertEquals("fusion_plus_quote_12345", result.getQuoteId());
        verify(apiService).getQuoteWithCustomPresets(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null, customPreset);
    }

    @Test
    void testGetQuoteWithCustomPresetsRx_Error() {
        // Given
        FusionPlusQuoteRequest request = createQuoteRequest();
        CustomPresetParams customPreset = createCustomPresetParams();
        RuntimeException exception = new RuntimeException("Invalid custom preset");

        when(apiService.getQuoteWithCustomPresets(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 100, null, null, customPreset))
                .thenReturn(Single.error(exception));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                fusionPlusQuoterService.getQuoteWithCustomPresetsRx(request, customPreset).blockingGet());
    }

    // ==================== ORDER BUILDING TESTS ====================

    @Test
    void testBuildQuoteTypedDataRx_Success() {
        // Given
        FusionPlusBuildOrderRequest request = createBuildOrderRequest();
        BuildOrderBody buildOrderBody = createBuildOrderBody();
        BuildOrderOutput expectedOutput = createBuildOrderOutput();

        when(apiService.buildQuoteTypedData(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, 100, "SDK-Test", null, null, null, null, "fast", buildOrderBody))
                .thenReturn(Single.just(expectedOutput));

        // When
        BuildOrderOutput result = fusionPlusQuoterService.buildQuoteTypedDataRx(request, buildOrderBody).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals("0x806039f5149065924ad52de616b50abff488c986716d052e9c160887bc09e559", result.getOrderHash());
        assertEquals("0x1234567890abcdef1234567890abcdef12345678", result.getExtension());
        assertNotNull(result.getTypedData());
        verify(apiService).buildQuoteTypedData(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, 100, "SDK-Test", null, null, null, null, "fast", buildOrderBody);
    }

    @Test
    void testBuildQuoteTypedDataAsync() throws Exception {
        // Given
        FusionPlusBuildOrderRequest request = createBuildOrderRequest();
        BuildOrderBody buildOrderBody = createBuildOrderBody();
        BuildOrderOutput expectedOutput = createBuildOrderOutput();

        when(apiService.buildQuoteTypedData(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, 100, "SDK-Test", null, null, null, null, "fast", buildOrderBody))
                .thenReturn(Single.just(expectedOutput));

        // When
        CompletableFuture<BuildOrderOutput> future = fusionPlusQuoterService.buildQuoteTypedDataAsync(request, buildOrderBody);
        BuildOrderOutput result = future.get();

        // Then
        assertNotNull(result);
        assertEquals("0x806039f5149065924ad52de616b50abff488c986716d052e9c160887bc09e559", result.getOrderHash());
        verify(apiService).buildQuoteTypedData(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, 100, "SDK-Test", null, null, null, null, "fast", buildOrderBody);
    }

    @Test
    void testBuildQuoteTypedData_Synchronous() throws OneInchException {
        // Given
        FusionPlusBuildOrderRequest request = createBuildOrderRequest();
        BuildOrderBody buildOrderBody = createBuildOrderBody();
        BuildOrderOutput expectedOutput = createBuildOrderOutput();

        when(apiService.buildQuoteTypedData(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, 100, "SDK-Test", null, null, null, null, "fast", buildOrderBody))
                .thenReturn(Single.just(expectedOutput));

        // When
        BuildOrderOutput result = fusionPlusQuoterService.buildQuoteTypedData(request, buildOrderBody);

        // Then
        assertNotNull(result);
        assertEquals("0x806039f5149065924ad52de616b50abff488c986716d052e9c160887bc09e559", result.getOrderHash());
        verify(apiService).buildQuoteTypedData(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, 100, "SDK-Test", null, null, null, null, "fast", buildOrderBody);
    }

    @Test
    void testBuildQuoteTypedDataRx_Error() {
        // Given
        FusionPlusBuildOrderRequest request = createBuildOrderRequest();
        BuildOrderBody buildOrderBody = createBuildOrderBody();
        RuntimeException exception = new RuntimeException("Invalid quote data");

        when(apiService.buildQuoteTypedData(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, 100, "SDK-Test", null, null, null, null, "fast", buildOrderBody))
                .thenReturn(Single.error(exception));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                fusionPlusQuoterService.buildQuoteTypedDataRx(request, buildOrderBody).blockingGet());
    }

    // ==================== PARAMETER VALIDATION TESTS ====================

    @Test
    void testGetQuoteRx_WithOptionalParameters() {
        // Given
        FusionPlusQuoteRequest request = FusionPlusQuoteRequest.builder()
                .srcChain(ETHEREUM_CHAIN_ID)
                .dstChain(POLYGON_CHAIN_ID)
                .srcTokenAddress(ETH_ADDRESS)
                .dstTokenAddress(USDC_POLYGON)
                .amount(SWAP_AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .enableEstimate(true)
                .fee(50) // 0.5% fee
                .isPermit2("0x123")
                .permit("0x456")
                .build();
        GetQuoteOutput expectedOutput = createQuoteOutput();

        when(apiService.getQuote(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 50, "0x123", "0x456"))
                .thenReturn(Single.just(expectedOutput));

        // When
        GetQuoteOutput result = fusionPlusQuoterService.getQuoteRx(request).blockingGet();

        // Then
        assertNotNull(result);
        verify(apiService).getQuote(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, true, 50, "0x123", "0x456");
    }

    @Test
    void testBuildQuoteTypedDataRx_WithAllOptionalParameters() {
        // Given
        FusionPlusBuildOrderRequest request = FusionPlusBuildOrderRequest.builder()
                .srcChain(ETHEREUM_CHAIN_ID)
                .dstChain(POLYGON_CHAIN_ID)
                .srcTokenAddress(ETH_ADDRESS)
                .dstTokenAddress(USDC_POLYGON)
                .amount(SWAP_AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .fee(100)
                .source("SDK-Test")
                .isPermit2("0x123")
                .isMobile("true")
                .feeReceiver("0x789")
                .permit("0x456")
                .preset("medium")
                .build();
        BuildOrderBody buildOrderBody = createBuildOrderBody();
        BuildOrderOutput expectedOutput = createBuildOrderOutput();

        when(apiService.buildQuoteTypedData(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, 100, "SDK-Test", "0x123", "true", "0x789", "0x456", "medium", buildOrderBody))
                .thenReturn(Single.just(expectedOutput));

        // When
        BuildOrderOutput result = fusionPlusQuoterService.buildQuoteTypedDataRx(request, buildOrderBody).blockingGet();

        // Then
        assertNotNull(result);
        verify(apiService).buildQuoteTypedData(
                ETHEREUM_CHAIN_ID, POLYGON_CHAIN_ID, ETH_ADDRESS, USDC_POLYGON,
                SWAP_AMOUNT, WALLET_ADDRESS, 100, "SDK-Test", "0x123", "true", "0x789", "0x456", "medium", buildOrderBody);
    }
}