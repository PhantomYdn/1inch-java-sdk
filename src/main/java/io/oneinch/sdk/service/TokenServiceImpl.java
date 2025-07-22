package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchTokenApiService;
import io.oneinch.sdk.client.OneInchTokenDetailsApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class TokenServiceImpl implements TokenService {
    
    private final OneInchTokenApiService tokenApiService;
    private final OneInchTokenDetailsApiService tokenDetailsApiService;
    
    public TokenServiceImpl(OneInchTokenApiService tokenApiService, 
                           OneInchTokenDetailsApiService tokenDetailsApiService) {
        this.tokenApiService = tokenApiService;
        this.tokenDetailsApiService = tokenDetailsApiService;
    }
    
    // Multi-chain token operations
    @Override
    public Map<String, ProviderTokenDto> getMultiChainTokens(TokenListRequest request) throws OneInchException {
        try {
            log.info("Getting multi-chain tokens with provider: {}, country: {}", 
                    request.getProvider(), request.getCountry());
            return getMultiChainTokensRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Multi-chain tokens request failed", e);
            throw new OneInchException("Failed to get multi-chain tokens", e);
        }
    }
    
    @Override
    public CompletableFuture<Map<String, ProviderTokenDto>> getMultiChainTokensAsync(TokenListRequest request) {
        return getMultiChainTokensRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<Map<String, ProviderTokenDto>> getMultiChainTokensRx(TokenListRequest request) {
        log.info("Getting multi-chain tokens (reactive) with provider: {}, country: {}", 
                request.getProvider(), request.getCountry());
        
        return tokenApiService.getMultiChainTokens(request.getProvider(), request.getCountry())
                .doOnSuccess(response -> log.debug("Multi-chain tokens retrieved: {} tokens", response.size()))
                .doOnError(error -> log.error("Multi-chain tokens request failed", error));
    }
    
    @Override
    public TokenListResponse getMultiChainTokenList(TokenListRequest request) throws OneInchException {
        try {
            log.info("Getting multi-chain token list with provider: {}, country: {}", 
                    request.getProvider(), request.getCountry());
            return getMultiChainTokenListRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Multi-chain token list request failed", e);
            throw new OneInchException("Failed to get multi-chain token list", e);
        }
    }
    
    @Override
    public CompletableFuture<TokenListResponse> getMultiChainTokenListAsync(TokenListRequest request) {
        return getMultiChainTokenListRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<TokenListResponse> getMultiChainTokenListRx(TokenListRequest request) {
        log.info("Getting multi-chain token list (reactive) with provider: {}, country: {}", 
                request.getProvider(), request.getCountry());
        
        return tokenApiService.getMultiChainTokenList(request.getProvider(), request.getCountry())
                .doOnSuccess(response -> log.debug("Multi-chain token list retrieved: {} tokens", 
                        response.getTokens() != null ? response.getTokens().size() : 0))
                .doOnError(error -> log.error("Multi-chain token list request failed", error));
    }
    
    // Chain-specific token operations
    @Override
    public Map<String, ProviderTokenDto> getTokens(TokenListRequest request) throws OneInchException {
        validateChainId(request.getChainId());
        try {
            log.info("Getting tokens for chain {} with provider: {}, country: {}", 
                    request.getChainId(), request.getProvider(), request.getCountry());
            return getTokensRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Tokens request failed for chain {}", request.getChainId(), e);
            throw new OneInchException("Failed to get tokens for chain " + request.getChainId(), e);
        }
    }
    
    @Override
    public CompletableFuture<Map<String, ProviderTokenDto>> getTokensAsync(TokenListRequest request) {
        return getTokensRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<Map<String, ProviderTokenDto>> getTokensRx(TokenListRequest request) {
        validateChainId(request.getChainId());
        log.info("Getting tokens (reactive) for chain {} with provider: {}, country: {}", 
                request.getChainId(), request.getProvider(), request.getCountry());
        
        return tokenApiService.getTokens(request.getChainId(), request.getProvider(), request.getCountry())
                .doOnSuccess(response -> log.debug("Tokens retrieved for chain {}: {} tokens", 
                        request.getChainId(), response.size()))
                .doOnError(error -> log.error("Tokens request failed for chain {}", request.getChainId(), error));
    }
    
    @Override
    public TokenListResponse getTokenList(TokenListRequest request) throws OneInchException {
        validateChainId(request.getChainId());
        try {
            log.info("Getting token list for chain {} with provider: {}, country: {}", 
                    request.getChainId(), request.getProvider(), request.getCountry());
            return getTokenListRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Token list request failed for chain {}", request.getChainId(), e);
            throw new OneInchException("Failed to get token list for chain " + request.getChainId(), e);
        }
    }
    
    @Override
    public CompletableFuture<TokenListResponse> getTokenListAsync(TokenListRequest request) {
        return getTokenListRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<TokenListResponse> getTokenListRx(TokenListRequest request) {
        validateChainId(request.getChainId());
        log.info("Getting token list (reactive) for chain {} with provider: {}, country: {}", 
                request.getChainId(), request.getProvider(), request.getCountry());
        
        return tokenApiService.getTokenList(request.getChainId(), request.getProvider(), request.getCountry())
                .doOnSuccess(response -> log.debug("Token list retrieved for chain {}: {} tokens", 
                        request.getChainId(), response.getTokens() != null ? response.getTokens().size() : 0))
                .doOnError(error -> log.error("Token list request failed for chain {}", request.getChainId(), error));
    }
    
    // Token search operations
    @Override
    public List<TokenDto> searchMultiChainTokens(TokenSearchRequest request) throws OneInchException {
        try {
            log.info("Searching multi-chain tokens with query: {}, limit: {}", 
                    request.getQuery(), request.getLimit());
            return searchMultiChainTokensRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Multi-chain token search failed", e);
            throw new OneInchException("Failed to search multi-chain tokens", e);
        }
    }
    
    @Override
    public CompletableFuture<List<TokenDto>> searchMultiChainTokensAsync(TokenSearchRequest request) {
        return searchMultiChainTokensRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<List<TokenDto>> searchMultiChainTokensRx(TokenSearchRequest request) {
        log.info("Searching multi-chain tokens (reactive) with query: {}, limit: {}", 
                request.getQuery(), request.getLimit());
        
        return tokenApiService.searchMultiChainTokens(
                request.getQuery(),
                request.getIgnoreListed(),
                request.getOnlyPositiveRating(),
                request.getLimit())
                .doOnSuccess(response -> log.debug("Multi-chain token search returned {} results", response.size()))
                .doOnError(error -> log.error("Multi-chain token search failed", error));
    }
    
    @Override
    public List<TokenDto> searchTokens(TokenSearchRequest request) throws OneInchException {
        validateChainId(request.getChainId());
        try {
            log.info("Searching tokens on chain {} with query: {}, limit: {}", 
                    request.getChainId(), request.getQuery(), request.getLimit());
            return searchTokensRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Token search failed for chain {}", request.getChainId(), e);
            throw new OneInchException("Failed to search tokens on chain " + request.getChainId(), e);
        }
    }
    
    @Override
    public CompletableFuture<List<TokenDto>> searchTokensAsync(TokenSearchRequest request) {
        return searchTokensRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<List<TokenDto>> searchTokensRx(TokenSearchRequest request) {
        validateChainId(request.getChainId());
        log.info("Searching tokens (reactive) on chain {} with query: {}, limit: {}", 
                request.getChainId(), request.getQuery(), request.getLimit());
        
        return tokenApiService.searchTokens(
                request.getChainId(),
                request.getQuery(),
                request.getIgnoreListed(),
                request.getOnlyPositiveRating(),
                request.getLimit())
                .doOnSuccess(response -> log.debug("Token search on chain {} returned {} results", 
                        request.getChainId(), response.size()))
                .doOnError(error -> log.error("Token search failed for chain {}", request.getChainId(), error));
    }
    
    // Custom token operations
    @Override
    public Map<String, TokenInfo> getCustomTokens(CustomTokenRequest request) throws OneInchException {
        validateChainId(request.getChainId());
        validateAddresses(request.getAddresses());
        try {
            log.info("Getting custom tokens for chain {} with addresses: {}", 
                    request.getChainId(), request.getAddresses());
            return getCustomTokensRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Custom tokens request failed for chain {}", request.getChainId(), e);
            throw new OneInchException("Failed to get custom tokens for chain " + request.getChainId(), e);
        }
    }
    
    @Override
    public CompletableFuture<Map<String, TokenInfo>> getCustomTokensAsync(CustomTokenRequest request) {
        return getCustomTokensRx(request)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<Map<String, TokenInfo>> getCustomTokensRx(CustomTokenRequest request) {
        validateChainId(request.getChainId());
        validateAddresses(request.getAddresses());
        log.info("Getting custom tokens (reactive) for chain {} with addresses: {}", 
                request.getChainId(), request.getAddresses());
        
        return tokenApiService.getCustomTokens(request.getChainId(), request.getAddresses())
                .doOnSuccess(response -> log.debug("Custom tokens retrieved for chain {}: {} tokens", 
                        request.getChainId(), response.size()))
                .doOnError(error -> log.error("Custom tokens request failed for chain {}", 
                        request.getChainId(), error));
    }
    
    @Override
    public TokenDto getCustomToken(Integer chainId, String address) throws OneInchException {
        validateChainId(chainId);
        validateAddress(address);
        try {
            log.info("Getting custom token for chain {} with address: {}", chainId, address);
            return getCustomTokenRx(chainId, address).blockingGet();
        } catch (Exception e) {
            log.error("Custom token request failed for chain {} and address {}", chainId, address, e);
            throw new OneInchException("Failed to get custom token for chain " + chainId + " and address " + address, e);
        }
    }
    
    @Override
    public CompletableFuture<TokenDto> getCustomTokenAsync(Integer chainId, String address) {
        return getCustomTokenRx(chainId, address)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    @Override
    public Single<TokenDto> getCustomTokenRx(Integer chainId, String address) {
        validateChainId(chainId);
        validateAddress(address);
        log.info("Getting custom token (reactive) for chain {} with address: {}", chainId, address);
        
        return tokenApiService.getCustomToken(chainId, address)
                .doOnSuccess(response -> log.debug("Custom token retrieved for chain {} and address {}: {}", 
                        chainId, address, response.getSymbol()))
                .doOnError(error -> log.error("Custom token request failed for chain {} and address {}", 
                        chainId, address, error));
    }
    
    // Token details operations
    @Override
    public TokenDetailsResponse getNativeTokenDetails(TokenDetailsRequest request) throws OneInchException {
        validateChainId(request.getChainId());
        try {
            log.info("Getting native token details for chain {} with provider: {}", 
                    request.getChainId(), request.getProvider());
            return getNativeTokenDetailsRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Native token details request failed for chain {}", request.getChainId(), e);
            throw new OneInchException("Failed to get native token details for chain " + request.getChainId(), e);
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
        validateChainId(request.getChainId());
        log.info("Getting native token details (reactive) for chain {} with provider: {}", 
                request.getChainId(), request.getProvider());
        
        return tokenDetailsApiService.getNativeTokenDetails(request.getChainId(), request.getProvider())
                .doOnSuccess(response -> log.debug("Native token details retrieved for chain {}", 
                        request.getChainId()))
                .doOnError(error -> log.error("Native token details request failed for chain {}", 
                        request.getChainId(), error));
    }
    
    @Override
    public TokenDetailsResponse getTokenDetails(TokenDetailsRequest request) throws OneInchException {
        validateChainId(request.getChainId());
        validateAddress(request.getContractAddress());
        try {
            log.info("Getting token details for chain {} and contract {} with provider: {}", 
                    request.getChainId(), request.getContractAddress(), request.getProvider());
            return getTokenDetailsRx(request).blockingGet();
        } catch (Exception e) {
            log.error("Token details request failed for chain {} and contract {}", 
                    request.getChainId(), request.getContractAddress(), e);
            throw new OneInchException("Failed to get token details for chain " + request.getChainId() 
                    + " and contract " + request.getContractAddress(), e);
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
        validateChainId(request.getChainId());
        validateAddress(request.getContractAddress());
        log.info("Getting token details (reactive) for chain {} and contract {} with provider: {}", 
                request.getChainId(), request.getContractAddress(), request.getProvider());
        
        return tokenDetailsApiService.getTokenDetails(request.getChainId(), 
                request.getContractAddress(), request.getProvider())
                .doOnSuccess(response -> log.debug("Token details retrieved for chain {} and contract {}", 
                        request.getChainId(), request.getContractAddress()))
                .doOnError(error -> log.error("Token details request failed for chain {} and contract {}", 
                        request.getChainId(), request.getContractAddress(), error));
    }
    
    // Validation methods
    private void validateChainId(Integer chainId) {
        if (chainId == null) {
            throw new IllegalArgumentException("Chain ID cannot be null");
        }
        if (chainId <= 0) {
            throw new IllegalArgumentException("Chain ID must be positive");
        }
    }
    
    private void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
    }
    
    private void validateAddresses(List<String> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            throw new IllegalArgumentException("Addresses list cannot be null or empty");
        }
        for (String address : addresses) {
            validateAddress(address);
        }
    }
}