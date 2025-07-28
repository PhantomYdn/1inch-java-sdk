package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchFusionQuoterApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.fusion.*;
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
class FusionQuoterServiceImplTest {

    @Mock
    private OneInchFusionQuoterApiService apiService;

    private FusionQuoterServiceImpl fusionQuoterService;

    private static final Integer CHAIN_ID = 1; // Ethereum
    private static final String FROM_TOKEN = "0xA0b86a33E6aB6b6ce4e5a5B7db2e8Df6b1D2b9C7"; // USDC
    private static final String TO_TOKEN = "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"; // ETH
    private static final String AMOUNT = "1000000000"; // 1000 USDC
    private static final String WALLET_ADDRESS = "0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8";
    private static final String QUOTE_ID = "quote_12345";

    @BeforeEach
    void setUp() {
        fusionQuoterService = new FusionQuoterServiceImpl(apiService);
    }

    // ==================== STANDARD QUOTES TESTS ====================

    @Test
    void testGetQuoteRx_Success() {
        // Given
        FusionQuoteRequest request = FusionQuoteRequest.builder()
                .chainId(CHAIN_ID)
                .fromTokenAddress(FROM_TOKEN)
                .toTokenAddress(TO_TOKEN)
                .amount(AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .enableEstimate(true)
                .fee(100) // 1%
                .showDestAmountMinusFee(true)
                .surplus(true)
                .build();

        ResolverFee resolverFee = ResolverFee.builder()
                .receiver("0x1111111254EEB25477B68fb85Ed929f73A960582")
                .bps(50)
                .whitelistDiscountPercent(10)
                .build();

        PresetClass fastPreset = PresetClass.builder()
                .bankFee("1000000")
                .auctionDuration(600) // 10 minutes
                .startAuctionIn(30)
                .initialRateBump(1000)
                .auctionStartAmount("500000000000000000")
                .startAmount("500000000000000000")
                .auctionEndAmount("400000000000000000")
                .tokenFee("500000")
                .estP(0.95)
                .points(Arrays.asList(
                    AuctionPointClass.builder().delay(0).coefficient(1000).build(),
                    AuctionPointClass.builder().delay(300).coefficient(500).build()
                ))
                .allowPartialFills(true)
                .allowMultipleFills(false)
                .build();

        QuotePresetsClass presets = QuotePresetsClass.builder()
                .fast(fastPreset)
                .medium(fastPreset) // Simplified for test
                .slow(fastPreset) // Simplified for test
                .build();

        TokenPairValue prices = TokenPairValue.builder()
                .usd(PairCurrencyValue.builder()
                        .fromToken("1.00")
                        .toToken("2000.00")
                        .build())
                .build();

        GetQuoteOutput expectedResponse = GetQuoteOutput.builder()
                .quoteId(QUOTE_ID)
                .fromTokenAmount(AMOUNT)
                .toTokenAmount("500000000000000000") // ~0.5 ETH
                .feeToken(TO_TOKEN)
                .fee(resolverFee)
                .integratorFee(100)
                .presets(presets)
                .settlementAddress("0x1111111254EEB25477B68fb85Ed929f73A960582")
                .whitelist(Arrays.asList("0xResolver1", "0xResolver2"))
                .recommendedPreset(PresetType.FAST)
                .suggested(true)
                .prices(prices)
                .volume(prices)
                .surplusFee(0.5)
                .build();

        when(apiService.getQuote(
                CHAIN_ID, FROM_TOKEN, TO_TOKEN, AMOUNT, WALLET_ADDRESS,
                true, 100, true, null, true, null, null, null))
                .thenReturn(Single.just(expectedResponse));

        // When
        GetQuoteOutput result = fusionQuoterService.getQuoteRx(request).blockingGet();

        // Then
        assertEquals(expectedResponse, result);
        assertEquals(QUOTE_ID, result.getQuoteId());
        assertEquals(PresetType.FAST, result.getRecommendedPreset());
        assertTrue(result.getSuggested());
        verify(apiService).getQuote(
                CHAIN_ID, FROM_TOKEN, TO_TOKEN, AMOUNT, WALLET_ADDRESS,
                true, 100, true, null, true, null, null, null);
    }

    @Test
    void testGetQuoteRx_Error() {
        // Given
        FusionQuoteRequest request = FusionQuoteRequest.builder()
                .chainId(CHAIN_ID)
                .fromTokenAddress(FROM_TOKEN)
                .toTokenAddress(TO_TOKEN)
                .amount(AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .enableEstimate(false)
                .build();

        RuntimeException exception = new RuntimeException("API Error");
        when(apiService.getQuote(
                CHAIN_ID, FROM_TOKEN, TO_TOKEN, AMOUNT, WALLET_ADDRESS,
                false, null, null, null, null, null, null, null))
                .thenReturn(Single.error(exception));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                fusionQuoterService.getQuoteRx(request).blockingGet());
    }

    @Test
    void testGetQuoteAsync() throws Exception {
        // Given
        FusionQuoteRequest request = FusionQuoteRequest.builder()
                .chainId(CHAIN_ID)
                .fromTokenAddress(FROM_TOKEN)
                .toTokenAddress(TO_TOKEN)
                .amount(AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .enableEstimate(true)
                .build();

        GetQuoteOutput expectedResponse = GetQuoteOutput.builder()
                .quoteId(QUOTE_ID)
                .fromTokenAmount(AMOUNT)
                .toTokenAmount("500000000000000000")
                .recommendedPreset(PresetType.MEDIUM)
                .suggested(true)
                .build();

        when(apiService.getQuote(
                CHAIN_ID, FROM_TOKEN, TO_TOKEN, AMOUNT, WALLET_ADDRESS,
                true, null, null, null, null, null, null, null))
                .thenReturn(Single.just(expectedResponse));

        // When
        CompletableFuture<GetQuoteOutput> future = fusionQuoterService.getQuoteAsync(request);
        GetQuoteOutput result = future.get();

        // Then
        assertEquals(expectedResponse, result);
        assertEquals(QUOTE_ID, result.getQuoteId());
    }

    @Test
    void testGetQuote_Synchronous() throws OneInchException {
        // Given
        FusionQuoteRequest request = FusionQuoteRequest.builder()
                .chainId(CHAIN_ID)
                .fromTokenAddress(FROM_TOKEN)
                .toTokenAddress(TO_TOKEN)
                .amount(AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .enableEstimate(false)
                .build();

        GetQuoteOutput expectedResponse = GetQuoteOutput.builder()
                .quoteId(QUOTE_ID)
                .fromTokenAmount(AMOUNT)
                .toTokenAmount("500000000000000000")
                .recommendedPreset(PresetType.SLOW)
                .suggested(false)
                .build();

        when(apiService.getQuote(
                CHAIN_ID, FROM_TOKEN, TO_TOKEN, AMOUNT, WALLET_ADDRESS,
                false, null, null, null, null, null, null, null))
                .thenReturn(Single.just(expectedResponse));

        // When
        GetQuoteOutput result = fusionQuoterService.getQuote(request);

        // Then
        assertEquals(expectedResponse, result);
    }

    // ==================== CUSTOM PRESET QUOTES TESTS ====================

    @Test
    void testGetQuoteWithCustomPresetsRx_Success() {
        // Given
        FusionQuoteRequest request = FusionQuoteRequest.builder()
                .chainId(CHAIN_ID)
                .fromTokenAddress(FROM_TOKEN)
                .toTokenAddress(TO_TOKEN)
                .amount(AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .enableEstimate(true)
                .surplus(true)
                .build();

        CustomPresetInput customPreset = CustomPresetInput.builder()
                .auctionDuration(900) // 15 minutes
                .auctionStartAmount(600000000000000000L) // 0.6 ETH
                .auctionEndAmount(450000000000000000L) // 0.45 ETH
                .points(Arrays.asList("0:1000", "450:500", "900:0"))
                .build();

        PresetClass customPresetClass = PresetClass.builder()
                .bankFee("2000000")
                .auctionDuration(900)
                .startAuctionIn(60)
                .initialRateBump(1200)
                .auctionStartAmount("600000000000000000")
                .startAmount("600000000000000000")
                .auctionEndAmount("450000000000000000")
                .tokenFee("750000")
                .estP(0.88)
                .points(Arrays.asList(
                    AuctionPointClass.builder().delay(0).coefficient(1200).build(),
                    AuctionPointClass.builder().delay(450).coefficient(600).build(),
                    AuctionPointClass.builder().delay(900).coefficient(0).build()
                ))
                .allowPartialFills(true)
                .allowMultipleFills(true)
                .build();

        QuotePresetsClass presets = QuotePresetsClass.builder()
                .fast(customPresetClass) // Simplified
                .medium(customPresetClass) // Simplified
                .slow(customPresetClass) // Simplified
                .custom(customPresetClass)
                .build();

        GetQuoteOutput expectedResponse = GetQuoteOutput.builder()
                .quoteId(QUOTE_ID + "_custom")
                .fromTokenAmount(AMOUNT)
                .toTokenAmount("525000000000000000") // ~0.525 ETH
                .presets(presets)
                .recommendedPreset(PresetType.CUSTOM)
                .suggested(true)
                .build();

        when(apiService.getQuoteWithCustomPresets(
                CHAIN_ID, FROM_TOKEN, TO_TOKEN, AMOUNT, WALLET_ADDRESS,
                true, null, null, null, true, null, null, customPreset))
                .thenReturn(Single.just(expectedResponse));

        // When
        GetQuoteOutput result = fusionQuoterService.getQuoteWithCustomPresetsRx(request, customPreset).blockingGet();

        // Then
        assertEquals(expectedResponse, result);
        assertEquals(QUOTE_ID + "_custom", result.getQuoteId());
        assertEquals(PresetType.CUSTOM, result.getRecommendedPreset());
        assertNotNull(result.getPresets().getCustom());
        assertEquals(900, result.getPresets().getCustom().getAuctionDuration());
        verify(apiService).getQuoteWithCustomPresets(
                CHAIN_ID, FROM_TOKEN, TO_TOKEN, AMOUNT, WALLET_ADDRESS,
                true, null, null, null, true, null, null, customPreset);
    }

    @Test
    void testGetQuoteWithCustomPresetsRx_Error() {
        // Given
        FusionQuoteRequest request = FusionQuoteRequest.builder()
                .chainId(CHAIN_ID)
                .fromTokenAddress(FROM_TOKEN)
                .toTokenAddress(TO_TOKEN)
                .amount(AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .enableEstimate(false)
                .build();

        CustomPresetInput customPreset = CustomPresetInput.builder()
                .auctionDuration(1200)
                .auctionStartAmount(500000000000000000L)
                .auctionEndAmount(400000000000000000L)
                .build();

        RuntimeException exception = new RuntimeException("Custom preset validation error");
        when(apiService.getQuoteWithCustomPresets(
                CHAIN_ID, FROM_TOKEN, TO_TOKEN, AMOUNT, WALLET_ADDRESS,
                false, null, null, null, null, null, null, customPreset))
                .thenReturn(Single.error(exception));

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                fusionQuoterService.getQuoteWithCustomPresetsRx(request, customPreset).blockingGet());
    }

    @Test
    void testGetQuoteWithCustomPresetsAsync() throws Exception {
        // Given
        FusionQuoteRequest request = FusionQuoteRequest.builder()
                .chainId(CHAIN_ID)
                .fromTokenAddress(FROM_TOKEN)
                .toTokenAddress(TO_TOKEN)
                .amount(AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .enableEstimate(true)
                .build();

        CustomPresetInput customPreset = CustomPresetInput.builder()
                .auctionDuration(600)
                .auctionStartAmount(500000000000000000L)
                .auctionEndAmount(400000000000000000L)
                .build();

        GetQuoteOutput expectedResponse = GetQuoteOutput.builder()
                .quoteId(QUOTE_ID + "_custom_async")
                .fromTokenAmount(AMOUNT)
                .toTokenAmount("475000000000000000")
                .recommendedPreset(PresetType.CUSTOM)
                .suggested(true)
                .build();

        when(apiService.getQuoteWithCustomPresets(
                CHAIN_ID, FROM_TOKEN, TO_TOKEN, AMOUNT, WALLET_ADDRESS,
                true, null, null, null, null, null, null, customPreset))
                .thenReturn(Single.just(expectedResponse));

        // When
        CompletableFuture<GetQuoteOutput> future = 
                fusionQuoterService.getQuoteWithCustomPresetsAsync(request, customPreset);
        GetQuoteOutput result = future.get();

        // Then
        assertEquals(expectedResponse, result);
        assertEquals(QUOTE_ID + "_custom_async", result.getQuoteId());
    }

    @Test
    void testGetQuoteWithCustomPresets_Synchronous() throws OneInchException {
        // Given
        FusionQuoteRequest request = FusionQuoteRequest.builder()
                .chainId(CHAIN_ID)
                .fromTokenAddress(FROM_TOKEN)
                .toTokenAddress(TO_TOKEN)
                .amount(AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .enableEstimate(false)
                .fee(50) // 0.5%
                .build();

        CustomPresetInput customPreset = CustomPresetInput.builder()
                .auctionDuration(1800) // 30 minutes
                .auctionStartAmount(550000000000000000L)
                .auctionEndAmount(420000000000000000L)
                .points(Arrays.asList("0:1000", "900:500", "1800:0"))
                .build();

        GetQuoteOutput expectedResponse = GetQuoteOutput.builder()
                .quoteId(QUOTE_ID + "_custom_sync")
                .fromTokenAmount(AMOUNT)
                .toTokenAmount("485000000000000000")
                .recommendedPreset(PresetType.CUSTOM)
                .suggested(false)
                .build();

        when(apiService.getQuoteWithCustomPresets(
                CHAIN_ID, FROM_TOKEN, TO_TOKEN, AMOUNT, WALLET_ADDRESS,
                false, 50, null, null, null, null, null, customPreset))
                .thenReturn(Single.just(expectedResponse));

        // When
        GetQuoteOutput result = fusionQuoterService.getQuoteWithCustomPresets(request, customPreset);

        // Then
        assertEquals(expectedResponse, result);
        assertFalse(result.getSuggested());
    }

    @Test
    void testGetQuoteRx_WithAllParameters() {
        // Given
        FusionQuoteRequest request = FusionQuoteRequest.builder()
                .chainId(CHAIN_ID)
                .fromTokenAddress(FROM_TOKEN)
                .toTokenAddress(TO_TOKEN)
                .amount(AMOUNT)
                .walletAddress(WALLET_ADDRESS)
                .enableEstimate(true)
                .fee(150) // 1.5%
                .showDestAmountMinusFee(false)
                .isPermit2("0x1234")
                .surplus(false)
                .permit("0xpermitsignature")
                .slippage(new Object()) // Mock object
                .source(new Object()) // Mock object
                .build();

        GetQuoteOutput expectedResponse = GetQuoteOutput.builder()
                .quoteId(QUOTE_ID + "_full")
                .fromTokenAmount(AMOUNT)
                .toTokenAmount("480000000000000000")
                .recommendedPreset(PresetType.MEDIUM)
                .suggested(true)
                .build();

        when(apiService.getQuote(
                eq(CHAIN_ID), eq(FROM_TOKEN), eq(TO_TOKEN), eq(AMOUNT), eq(WALLET_ADDRESS),
                eq(true), eq(150), eq(false), eq("0x1234"), eq(false), eq("0xpermitsignature"),
                any(), any()))
                .thenReturn(Single.just(expectedResponse));

        // When
        GetQuoteOutput result = fusionQuoterService.getQuoteRx(request).blockingGet();

        // Then
        assertEquals(expectedResponse, result);
        assertEquals(QUOTE_ID + "_full", result.getQuoteId());
        verify(apiService).getQuote(
                eq(CHAIN_ID), eq(FROM_TOKEN), eq(TO_TOKEN), eq(AMOUNT), eq(WALLET_ADDRESS),
                eq(true), eq(150), eq(false), eq("0x1234"), eq(false), eq("0xpermitsignature"),
                any(), any());
    }
}