package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.Token;
import io.oneinch.sdk.model.token.*;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface TokenService {
    
    // Multi-chain token operations
    /**
     * Get multi-chain whitelisted tokens (synchronous).
     * @param request Token list request parameters
     * @return List of token info
     * @throws OneInchException if the request fails
     */
    List<ProviderTokenDto> getMultiChainTokens(TokenListRequest request) throws OneInchException;
    
    /**
     * Get multi-chain whitelisted tokens (asynchronous with CompletableFuture).
     * @param request Token list request parameters
     * @return CompletableFuture containing token list
     */
    CompletableFuture<List<ProviderTokenDto>> getMultiChainTokensAsync(TokenListRequest request);
    
    /**
     * Get multi-chain whitelisted tokens (reactive with RxJava).
     * @param request Token list request parameters
     * @return Single containing token list
     */
    Single<List<ProviderTokenDto>> getMultiChainTokensRx(TokenListRequest request);
    
    /**
     * Get multi-chain whitelisted tokens in list format (synchronous).
     * @param request Token list request parameters
     * @return Token list response
     * @throws OneInchException if the request fails
     */
    TokenListResponse getMultiChainTokenList(TokenListRequest request) throws OneInchException;
    
    /**
     * Get multi-chain whitelisted tokens in list format (asynchronous with CompletableFuture).
     * @param request Token list request parameters
     * @return CompletableFuture containing token list response
     */
    CompletableFuture<TokenListResponse> getMultiChainTokenListAsync(TokenListRequest request);
    
    /**
     * Get multi-chain whitelisted tokens in list format (reactive with RxJava).
     * @param request Token list request parameters
     * @return Single containing token list response
     */
    Single<TokenListResponse> getMultiChainTokenListRx(TokenListRequest request);
    
    // Chain-specific token operations
    /**
     * Get chain-specific whitelisted tokens (synchronous).
     * @param request Token list request parameters (chainId required)
     * @return Map of token address to token info
     * @throws OneInchException if the request fails
     */
    Map<String, ProviderTokenDto> getTokens(TokenListRequest request) throws OneInchException;
    
    /**
     * Get chain-specific whitelisted tokens (asynchronous with CompletableFuture).
     * @param request Token list request parameters (chainId required)
     * @return CompletableFuture containing token map
     */
    CompletableFuture<Map<String, ProviderTokenDto>> getTokensAsync(TokenListRequest request);
    
    /**
     * Get chain-specific whitelisted tokens (reactive with RxJava).
     * @param request Token list request parameters (chainId required)
     * @return Single containing token map
     */
    Single<Map<String, ProviderTokenDto>> getTokensRx(TokenListRequest request);
    
    /**
     * Get chain-specific whitelisted tokens in list format (synchronous).
     * @param request Token list request parameters (chainId required)
     * @return Token list response
     * @throws OneInchException if the request fails
     */
    TokenListResponse getTokenList(TokenListRequest request) throws OneInchException;
    
    /**
     * Get chain-specific whitelisted tokens in list format (asynchronous with CompletableFuture).
     * @param request Token list request parameters (chainId required)
     * @return CompletableFuture containing token list response
     */
    CompletableFuture<TokenListResponse> getTokenListAsync(TokenListRequest request);
    
    /**
     * Get chain-specific whitelisted tokens in list format (reactive with RxJava).
     * @param request Token list request parameters (chainId required)
     * @return Single containing token list response
     */
    Single<TokenListResponse> getTokenListRx(TokenListRequest request);
    
    // Token search operations
    /**
     * Search tokens across multiple chains (synchronous).
     * @param request Token search request parameters
     * @return List of matching tokens
     * @throws OneInchException if the request fails
     */
    List<Token> searchMultiChainTokens(TokenSearchRequest request) throws OneInchException;
    
    /**
     * Search tokens across multiple chains (asynchronous with CompletableFuture).
     * @param request Token search request parameters
     * @return CompletableFuture containing list of matching tokens
     */
    CompletableFuture<List<Token>> searchMultiChainTokensAsync(TokenSearchRequest request);
    
    /**
     * Search tokens across multiple chains (reactive with RxJava).
     * @param request Token search request parameters
     * @return Single containing list of matching tokens
     */
    Single<List<Token>> searchMultiChainTokensRx(TokenSearchRequest request);
    
    /**
     * Search tokens on specific chain (synchronous).
     * @param request Token search request parameters (chainId required)
     * @return List of matching tokens
     * @throws OneInchException if the request fails
     */
    List<Token> searchTokens(TokenSearchRequest request) throws OneInchException;
    
    /**
     * Search tokens on specific chain (asynchronous with CompletableFuture).
     * @param request Token search request parameters (chainId required)
     * @return CompletableFuture containing list of matching tokens
     */
    CompletableFuture<List<Token>> searchTokensAsync(TokenSearchRequest request);
    
    /**
     * Search tokens on specific chain (reactive with RxJava).
     * @param request Token search request parameters (chainId required)
     * @return Single containing list of matching tokens
     */
    Single<List<Token>> searchTokensRx(TokenSearchRequest request);
    
    // Custom token operations
    /**
     * Get multiple custom tokens by addresses (synchronous).
     * @param request Custom token request parameters
     * @return Map of token address to token info
     * @throws OneInchException if the request fails
     */
    Map<String, Token> getCustomTokens(CustomTokenRequest request) throws OneInchException;
    
    /**
     * Get multiple custom tokens by addresses (asynchronous with CompletableFuture).
     * @param request Custom token request parameters
     * @return CompletableFuture containing token map
     */
    CompletableFuture<Map<String, Token>> getCustomTokensAsync(CustomTokenRequest request);
    
    /**
     * Get multiple custom tokens by addresses (reactive with RxJava).
     * @param request Custom token request parameters
     * @return Single containing token map
     */
    Single<Map<String, Token>> getCustomTokensRx(CustomTokenRequest request);
    
    /**
     * Get single custom token by address (synchronous).
     * @param chainId Chain ID
     * @param address Token contract address
     * @return Token details
     * @throws OneInchException if the request fails
     */
    Token getCustomToken(Integer chainId, String address) throws OneInchException;
    
    /**
     * Get single custom token by address (asynchronous with CompletableFuture).
     * @param chainId Chain ID
     * @param address Token contract address
     * @return CompletableFuture containing token details
     */
    CompletableFuture<Token> getCustomTokenAsync(Integer chainId, String address);
    
    /**
     * Get single custom token by address (reactive with RxJava).
     * @param chainId Chain ID
     * @param address Token contract address
     * @return Single containing token details
     */
    Single<Token> getCustomTokenRx(Integer chainId, String address);
    
}