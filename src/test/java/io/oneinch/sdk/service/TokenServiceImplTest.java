package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchTokenApiService;
import io.oneinch.sdk.client.OneInchTokenDetailsApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
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
class TokenServiceImplTest {

    @Mock
    private OneInchTokenApiService tokenApiService;

    @Mock
    private OneInchTokenDetailsApiService tokenDetailsApiService;

    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl(tokenApiService, tokenDetailsApiService);
    }

    @Test
    void testGetMultiChainTokensRx_Success() {
        // Given
        TokenListRequest request = TokenListRequest.builder()
                .provider("1inch")
                .country("US")
                .build();

        ProviderTokenDto token = new ProviderTokenDto();
        token.setSymbol("1INCH");
        token.setName("1inch");
        token.setAddress("0x111111111117dc0aa78b770fa6a738034120c302");

        Map<String, ProviderTokenDto> expectedResponse = Map.of("0x111111111117dc0aa78b770fa6a738034120c302", token);

        when(tokenApiService.getMultiChainTokens(eq("1inch"), eq("US")))
                .thenReturn(Single.just(expectedResponse));

        // When
        Map<String, ProviderTokenDto> result = tokenService.getMultiChainTokensRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("0x111111111117dc0aa78b770fa6a738034120c302"));
        assertEquals("1INCH", result.get("0x111111111117dc0aa78b770fa6a738034120c302").getSymbol());
        verify(tokenApiService).getMultiChainTokens("1inch", "US");
    }

    @Test
    void testGetMultiChainTokens_Success() throws OneInchException {
        // Given
        TokenListRequest request = TokenListRequest.builder()
                .provider("1inch")
                .build();

        ProviderTokenDto token = new ProviderTokenDto();
        token.setSymbol("ETH");
        token.setName("Ethereum");

        Map<String, ProviderTokenDto> expectedResponse = Map.of("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", token);

        when(tokenApiService.getMultiChainTokens(eq("1inch"), any()))
                .thenReturn(Single.just(expectedResponse));

        // When
        Map<String, ProviderTokenDto> result = tokenService.getMultiChainTokens(request);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tokenApiService).getMultiChainTokens("1inch", null);
    }

    @Test
    void testGetMultiChainTokensAsync_Success() {
        // Given
        TokenListRequest request = TokenListRequest.builder()
                .provider("1inch")
                .country("US")
                .build();

        ProviderTokenDto token = new ProviderTokenDto();
        token.setSymbol("USDC");
        
        Map<String, ProviderTokenDto> expectedResponse = Map.of("0xa0b86a33e6ab6b6ce4e5a5b7db2e8df6b1d2b9c7", token);

        when(tokenApiService.getMultiChainTokens(any(), any()))
                .thenReturn(Single.just(expectedResponse));

        // When
        CompletableFuture<Map<String, ProviderTokenDto>> future = tokenService.getMultiChainTokensAsync(request);
        Map<String, ProviderTokenDto> result = future.join();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tokenApiService).getMultiChainTokens("1inch", "US");
    }

    @Test
    void testGetTokensRx_Success() {
        // Given
        TokenListRequest request = TokenListRequest.builder()
                .chainId(1)
                .provider("1inch")
                .build();

        ProviderTokenDto token = new ProviderTokenDto();
        token.setChainId(1);
        token.setSymbol("WETH");
        
        Map<String, ProviderTokenDto> expectedResponse = Map.of("0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2", token);

        when(tokenApiService.getTokens(eq(1), eq("1inch"), any()))
                .thenReturn(Single.just(expectedResponse));

        // When
        Map<String, ProviderTokenDto> result = tokenService.getTokensRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tokenApiService).getTokens(1, "1inch", null);
    }

    @Test
    void testGetTokensRx_InvalidChainId() {
        // Given
        TokenListRequest request = TokenListRequest.builder()
                .chainId(null)
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> tokenService.getTokensRx(request));
        verifyNoInteractions(tokenApiService);
    }

    @Test
    void testSearchMultiChainTokensRx_Success() {
        // Given
        TokenSearchRequest request = TokenSearchRequest.builder()
                .query("1inch")
                .onlyPositiveRating(true)
                .limit(10)
                .build();

        TokenDto token = new TokenDto();
        token.setSymbol("1INCH");
        token.setName("1inch");

        List<TokenDto> expectedResponse = List.of(token);

        when(tokenApiService.searchMultiChainTokens(eq("1inch"), any(), eq(true), eq(10)))
                .thenReturn(Single.just(expectedResponse));

        // When
        List<TokenDto> result = tokenService.searchMultiChainTokensRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1INCH", result.get(0).getSymbol());
        verify(tokenApiService).searchMultiChainTokens("1inch", null, true, 10);
    }

    @Test
    void testGetCustomTokenRx_Success() {
        // Given
        Integer chainId = 1;
        String address = "0x111111111117dc0aa78b770fa6a738034120c302";

        TokenDto expectedToken = new TokenDto();
        expectedToken.setChainId(chainId);
        expectedToken.setAddress(address);
        expectedToken.setSymbol("1INCH");

        when(tokenApiService.getCustomToken(eq(chainId), eq(address)))
                .thenReturn(Single.just(expectedToken));

        // When
        TokenDto result = tokenService.getCustomTokenRx(chainId, address).blockingGet();

        // Then
        assertNotNull(result);
        assertEquals(chainId, result.getChainId());
        assertEquals(address, result.getAddress());
        assertEquals("1INCH", result.getSymbol());
        verify(tokenApiService).getCustomToken(chainId, address);
    }

    @Test
    void testGetCustomToken_InvalidAddress() {
        // Given
        Integer chainId = 1;
        String address = "";

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> tokenService.getCustomToken(chainId, address));
        verifyNoInteractions(tokenApiService);
    }

    @Test
    void testGetTokenDetailsRx_Success() {
        // Given
        TokenDetailsRequest request = TokenDetailsRequest.builder()
                .chainId(1)
                .contractAddress("0x111111111117dc0aa78b770fa6a738034120c302")
                .provider("coinmarketcap")
                .build();

        TokenDetailsResponse expectedResponse = new TokenDetailsResponse();
        AssetsResponse assets = new AssetsResponse();
        assets.setName("1inch");
        expectedResponse.setAssets(assets);

        when(tokenDetailsApiService.getTokenDetails(eq(1), 
                eq("0x111111111117dc0aa78b770fa6a738034120c302"), eq("coinmarketcap")))
                .thenReturn(Single.just(expectedResponse));

        // When
        TokenDetailsResponse result = tokenService.getTokenDetailsRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertNotNull(result.getAssets());
        assertEquals("1inch", result.getAssets().getName());
        verify(tokenDetailsApiService).getTokenDetails(1, 
                "0x111111111117dc0aa78b770fa6a738034120c302", "coinmarketcap");
    }

    @Test
    void testGetNativeTokenDetailsRx_Success() {
        // Given
        TokenDetailsRequest request = TokenDetailsRequest.builder()
                .chainId(1)
                .provider("coingecko")
                .build();

        TokenDetailsResponse expectedResponse = new TokenDetailsResponse();
        AssetsResponse assets = new AssetsResponse();
        assets.setName("Ethereum");
        expectedResponse.setAssets(assets);

        when(tokenDetailsApiService.getNativeTokenDetails(eq(1), eq("coingecko")))
                .thenReturn(Single.just(expectedResponse));

        // When
        TokenDetailsResponse result = tokenService.getNativeTokenDetailsRx(request).blockingGet();

        // Then
        assertNotNull(result);
        assertNotNull(result.getAssets());
        assertEquals("Ethereum", result.getAssets().getName());
        verify(tokenDetailsApiService).getNativeTokenDetails(1, "coingecko");
    }

    @Test
    void testGetMultiChainTokenList_Error() {
        // Given
        TokenListRequest request = TokenListRequest.builder()
                .provider("1inch")
                .build();

        when(tokenApiService.getMultiChainTokenList(any(), any()))
                .thenReturn(Single.error(new RuntimeException("API Error")));

        // When & Then
        assertThrows(OneInchException.class, () -> tokenService.getMultiChainTokenList(request));
        verify(tokenApiService).getMultiChainTokenList("1inch", null);
    }

    @Test
    void testGetTokenListAsync_Exception() {
        // Given
        TokenListRequest request = TokenListRequest.builder()
                .chainId(1)
                .provider("1inch")
                .build();

        when(tokenApiService.getTokenList(any(), any(), any()))
                .thenReturn(Single.error(new RuntimeException("API Error")));

        // When
        CompletableFuture<TokenListResponse> future = tokenService.getTokenListAsync(request);

        // Then
        assertThrows(CompletionException.class, future::join);
        verify(tokenApiService).getTokenList(1, "1inch", null);
    }
}