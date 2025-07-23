package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchTokenDetailsApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class TokenDetailsServiceImpl implements TokenDetailsService {
    
    private final OneInchTokenDetailsApiService tokenDetailsApiService;
    
    public TokenDetailsServiceImpl(OneInchTokenDetailsApiService tokenDetailsApiService) {
        this.tokenDetailsApiService = tokenDetailsApiService;
    }
    
    // Token details operations
    @Override
    public TokenDetailsResponse getNativeTokenDetails(TokenDetailsRequest request) throws OneInchException {
        try {
            log.info("Getting native token details for chain: {}, provider: {}", 
                    request.getChainId(), request.getProvider());
            return getNativeTokenDetailsRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Native token details request failed", e);
            throw new OneInchException("Failed to get native token details", e);
        }
    }
    
    @Override
    public CompletableFuture<TokenDetailsResponse> getNativeTokenDetailsAsync(TokenDetailsRequest request) {
        return getNativeTokenDetailsRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<TokenDetailsResponse> getNativeTokenDetailsRx(TokenDetailsRequest request) {
        log.info("Getting native token details (reactive) for chain: {}, provider: {}", 
                request.getChainId(), request.getProvider());
        
        return tokenDetailsApiService.getNativeTokenDetails(request.getChainId(), request.getProvider())
                .doOnSuccess(response -> log.debug("Native token details retrieved successfully for chain: {}", 
                        request.getChainId()))
                .doOnError(error -> log.error("Native token details request failed", error));
    }
    
    @Override
    public TokenDetailsResponse getTokenDetails(TokenDetailsRequest request) throws OneInchException {
        try {
            log.info("Getting token details for chain: {}, address: {}, provider: {}", 
                    request.getChainId(), request.getContractAddress(), request.getProvider());
            return getTokenDetailsRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Token details request failed", e);
            throw new OneInchException("Failed to get token details", e);
        }
    }
    
    @Override
    public CompletableFuture<TokenDetailsResponse> getTokenDetailsAsync(TokenDetailsRequest request) {
        return getTokenDetailsRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<TokenDetailsResponse> getTokenDetailsRx(TokenDetailsRequest request) {
        if (request.getContractAddress() == null || request.getContractAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Contract address is required for token details");
        }
        
        log.info("Getting token details (reactive) for chain: {}, address: {}, provider: {}", 
                request.getChainId(), request.getContractAddress(), request.getProvider());
        
        return tokenDetailsApiService.getTokenDetails(request.getChainId(), 
                request.getContractAddress(), request.getProvider())
                .doOnSuccess(response -> log.debug("Token details retrieved successfully for address: {}", 
                        request.getContractAddress()))
                .doOnError(error -> log.error("Token details request failed", error));
    }
    
    // Chart operations - Range based
    @Override
    public ChartDataResponse getNativeTokenChartByRange(ChartRangeRequest request) throws OneInchException {
        try {
            log.info("Getting native token chart by range for chain: {}, from: {}, to: {}", 
                    request.getChainId(), request.getFrom(), request.getTo());
            return getNativeTokenChartByRangeRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Native token chart by range request failed", e);
            throw new OneInchException("Failed to get native token chart by range", e);
        }
    }
    
    @Override
    public CompletableFuture<ChartDataResponse> getNativeTokenChartByRangeAsync(ChartRangeRequest request) {
        return getNativeTokenChartByRangeRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<ChartDataResponse> getNativeTokenChartByRangeRx(ChartRangeRequest request) {
        log.info("Getting native token chart by range (reactive) for chain: {}, from: {}, to: {}", 
                request.getChainId(), request.getFrom(), request.getTo());
        
        return tokenDetailsApiService.getNativeTokenChartByRange(request.getChainId(), 
                request.getFrom(), request.getTo(), request.getProvider(), request.getFromTime())
                .doOnSuccess(response -> log.debug("Native token chart by range retrieved successfully"))
                .doOnError(error -> log.error("Native token chart by range request failed", error));
    }
    
    @Override
    public ChartDataResponse getTokenChartByRange(ChartRangeRequest request) throws OneInchException {
        try {
            log.info("Getting token chart by range for chain: {}, address: {}, from: {}, to: {}", 
                    request.getChainId(), request.getTokenAddress(), request.getFrom(), request.getTo());
            return getTokenChartByRangeRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Token chart by range request failed", e);
            throw new OneInchException("Failed to get token chart by range", e);
        }
    }
    
    @Override
    public CompletableFuture<ChartDataResponse> getTokenChartByRangeAsync(ChartRangeRequest request) {
        return getTokenChartByRangeRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<ChartDataResponse> getTokenChartByRangeRx(ChartRangeRequest request) {
        if (request.getTokenAddress() == null || request.getTokenAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Token address is required for token chart by range");
        }
        
        log.info("Getting token chart by range (reactive) for chain: {}, address: {}, from: {}, to: {}", 
                request.getChainId(), request.getTokenAddress(), request.getFrom(), request.getTo());
        
        return tokenDetailsApiService.getTokenChartByRange(request.getChainId(), 
                request.getTokenAddress(), request.getFrom(), request.getTo(), 
                request.getProvider(), request.getFromTime())
                .doOnSuccess(response -> log.debug("Token chart by range retrieved successfully"))
                .doOnError(error -> log.error("Token chart by range request failed", error));
    }
    
    // Chart operations - Interval based
    @Override
    public ChartDataResponse getNativeTokenChartByInterval(ChartIntervalRequest request) throws OneInchException {
        try {
            log.info("Getting native token chart by interval for chain: {}, interval: {}", 
                    request.getChainId(), request.getInterval());
            return getNativeTokenChartByIntervalRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Native token chart by interval request failed", e);
            throw new OneInchException("Failed to get native token chart by interval", e);
        }
    }
    
    @Override
    public CompletableFuture<ChartDataResponse> getNativeTokenChartByIntervalAsync(ChartIntervalRequest request) {
        return getNativeTokenChartByIntervalRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<ChartDataResponse> getNativeTokenChartByIntervalRx(ChartIntervalRequest request) {
        log.info("Getting native token chart by interval (reactive) for chain: {}, interval: {}", 
                request.getChainId(), request.getInterval());
        
        return tokenDetailsApiService.getNativeTokenChartByInterval(request.getChainId(), 
                request.getInterval(), request.getProvider(), request.getFromTime())
                .doOnSuccess(response -> log.debug("Native token chart by interval retrieved successfully"))
                .doOnError(error -> log.error("Native token chart by interval request failed", error));
    }
    
    @Override
    public ChartDataResponse getTokenChartByInterval(ChartIntervalRequest request) throws OneInchException {
        try {
            log.info("Getting token chart by interval for chain: {}, address: {}, interval: {}", 
                    request.getChainId(), request.getTokenAddress(), request.getInterval());
            return getTokenChartByIntervalRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Token chart by interval request failed", e);
            throw new OneInchException("Failed to get token chart by interval", e);
        }
    }
    
    @Override
    public CompletableFuture<ChartDataResponse> getTokenChartByIntervalAsync(ChartIntervalRequest request) {
        return getTokenChartByIntervalRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<ChartDataResponse> getTokenChartByIntervalRx(ChartIntervalRequest request) {
        if (request.getTokenAddress() == null || request.getTokenAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Token address is required for token chart by interval");
        }
        
        log.info("Getting token chart by interval (reactive) for chain: {}, address: {}, interval: {}", 
                request.getChainId(), request.getTokenAddress(), request.getInterval());
        
        return tokenDetailsApiService.getTokenChartByInterval(request.getChainId(), 
                request.getTokenAddress(), request.getInterval(), 
                request.getProvider(), request.getFromTime())
                .doOnSuccess(response -> log.debug("Token chart by interval retrieved successfully"))
                .doOnError(error -> log.error("Token chart by interval request failed", error));
    }
    
    // Price change operations
    @Override
    public TokenPriceChangeResponse getNativeTokenPriceChange(PriceChangeRequest request) throws OneInchException {
        try {
            log.info("Getting native token price change for chain: {}, interval: {}", 
                    request.getChainId(), request.getInterval());
            return getNativeTokenPriceChangeRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Native token price change request failed", e);
            throw new OneInchException("Failed to get native token price change", e);
        }
    }
    
    @Override
    public CompletableFuture<TokenPriceChangeResponse> getNativeTokenPriceChangeAsync(PriceChangeRequest request) {
        return getNativeTokenPriceChangeRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<TokenPriceChangeResponse> getNativeTokenPriceChangeRx(PriceChangeRequest request) {
        log.info("Getting native token price change (reactive) for chain: {}, interval: {}", 
                request.getChainId(), request.getInterval());
        
        return tokenDetailsApiService.getNativeTokenPriceChange(request.getChainId(), request.getInterval())
                .doOnSuccess(response -> log.debug("Native token price change retrieved successfully"))
                .doOnError(error -> log.error("Native token price change request failed", error));
    }
    
    @Override
    public TokenPriceChangeResponse getTokenPriceChange(PriceChangeRequest request) throws OneInchException {
        try {
            log.info("Getting token price change for chain: {}, address: {}, interval: {}", 
                    request.getChainId(), request.getTokenAddress(), request.getInterval());
            return getTokenPriceChangeRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Token price change request failed", e);
            throw new OneInchException("Failed to get token price change", e);
        }
    }
    
    @Override
    public CompletableFuture<TokenPriceChangeResponse> getTokenPriceChangeAsync(PriceChangeRequest request) {
        return getTokenPriceChangeRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<TokenPriceChangeResponse> getTokenPriceChangeRx(PriceChangeRequest request) {
        if (request.getTokenAddress() == null || request.getTokenAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Token address is required for token price change");
        }
        
        log.info("Getting token price change (reactive) for chain: {}, address: {}, interval: {}", 
                request.getChainId(), request.getTokenAddress(), request.getInterval());
        
        return tokenDetailsApiService.getTokenPriceChange(request.getChainId(), 
                request.getTokenAddress(), request.getInterval())
                .doOnSuccess(response -> log.debug("Token price change retrieved successfully"))
                .doOnError(error -> log.error("Token price change request failed", error));
    }
    
    @Override
    public List<TokenListPriceChangeResponse> getTokenListPriceChange(TokenListPriceChangeRequest request) throws OneInchException {
        try {
            log.info("Getting token list price change for chain: {}, interval: {}, tokens: {}", 
                    request.getChainId(), request.getInterval(), request.getTokenAddresses().size());
            return getTokenListPriceChangeRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Token list price change request failed", e);
            throw new OneInchException("Failed to get token list price change", e);
        }
    }
    
    @Override
    public CompletableFuture<List<TokenListPriceChangeResponse>> getTokenListPriceChangeAsync(TokenListPriceChangeRequest request) {
        return getTokenListPriceChangeRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<List<TokenListPriceChangeResponse>> getTokenListPriceChangeRx(TokenListPriceChangeRequest request) {
        log.info("Getting token list price change (reactive) for chain: {}, interval: {}, tokens: {}", 
                request.getChainId(), request.getInterval(), request.getTokenAddresses().size());
        
        // Create request body without chainId field
        TokenListPriceChangeRequest requestBody = TokenListPriceChangeRequest.builder()
                .tokenAddresses(request.getTokenAddresses())
                .interval(request.getInterval())
                .build();
        
        return tokenDetailsApiService.getTokenListPriceChange(request.getChainId(), requestBody)
                .doOnSuccess(response -> log.debug("Token list price change retrieved successfully: {} results", 
                        response.size()))
                .doOnError(error -> log.error("Token list price change request failed", error));
    }
}