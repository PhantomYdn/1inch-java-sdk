package io.oneinch.sdk.service;

import io.oneinch.sdk.client.HttpClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class SwapServiceImpl implements SwapService {
    
    private final HttpClient httpClient;
    
    @Override
    public QuoteResponse getQuote(QuoteRequest request) throws OneInchException {
        log.info("Getting quote for swap from {} to {} with amount {}", 
                request.getSrc(), request.getDst(), request.getAmount());
        
        Map<String, Object> params = buildQuoteParams(request);
        return httpClient.get("/quote", params, QuoteResponse.class);
    }
    
    @Override
    public CompletableFuture<QuoteResponse> getQuoteAsync(QuoteRequest request) {
        log.info("Getting quote async for swap from {} to {} with amount {}", 
                request.getSrc(), request.getDst(), request.getAmount());
        
        Map<String, Object> params = buildQuoteParams(request);
        return httpClient.getAsync("/quote", params, QuoteResponse.class);
    }
    
    @Override
    public SwapResponse getSwap(SwapRequest request) throws OneInchException {
        log.info("Getting swap data from {} to {} with amount {} for address {}", 
                request.getSrc(), request.getDst(), request.getAmount(), request.getFrom());
        
        Map<String, Object> params = buildSwapParams(request);
        return httpClient.get("/swap", params, SwapResponse.class);
    }
    
    @Override
    public CompletableFuture<SwapResponse> getSwapAsync(SwapRequest request) {
        log.info("Getting swap data async from {} to {} with amount {} for address {}", 
                request.getSrc(), request.getDst(), request.getAmount(), request.getFrom());
        
        Map<String, Object> params = buildSwapParams(request);
        return httpClient.getAsync("/swap", params, SwapResponse.class);
    }
    
    @Override
    public SpenderResponse getSpender() throws OneInchException {
        log.info("Getting spender address");
        return httpClient.get("/approve/spender", null, SpenderResponse.class);
    }
    
    @Override
    public CompletableFuture<SpenderResponse> getSpenderAsync() {
        log.info("Getting spender address async");
        return httpClient.getAsync("/approve/spender", null, SpenderResponse.class);
    }
    
    @Override
    public ApproveCallDataResponse getApproveTransaction(ApproveTransactionRequest request) throws OneInchException {
        log.info("Getting approve transaction data for token {} with amount {}", 
                request.getTokenAddress(), request.getAmount());
        
        Map<String, Object> params = new HashMap<>();
        params.put("tokenAddress", request.getTokenAddress());
        if (request.getAmount() != null) {
            params.put("amount", request.getAmount());
        }
        
        return httpClient.get("/approve/transaction", params, ApproveCallDataResponse.class);
    }
    
    @Override
    public CompletableFuture<ApproveCallDataResponse> getApproveTransactionAsync(ApproveTransactionRequest request) {
        log.info("Getting approve transaction data async for token {} with amount {}", 
                request.getTokenAddress(), request.getAmount());
        
        Map<String, Object> params = new HashMap<>();
        params.put("tokenAddress", request.getTokenAddress());
        if (request.getAmount() != null) {
            params.put("amount", request.getAmount());
        }
        
        return httpClient.getAsync("/approve/transaction", params, ApproveCallDataResponse.class);
    }
    
    @Override
    public AllowanceResponse getAllowance(AllowanceRequest request) throws OneInchException {
        log.info("Getting allowance for token {} and wallet {}", 
                request.getTokenAddress(), request.getWalletAddress());
        
        Map<String, Object> params = new HashMap<>();
        params.put("tokenAddress", request.getTokenAddress());
        params.put("walletAddress", request.getWalletAddress());
        
        return httpClient.get("/approve/allowance", params, AllowanceResponse.class);
    }
    
    @Override
    public CompletableFuture<AllowanceResponse> getAllowanceAsync(AllowanceRequest request) {
        log.info("Getting allowance async for token {} and wallet {}", 
                request.getTokenAddress(), request.getWalletAddress());
        
        Map<String, Object> params = new HashMap<>();
        params.put("tokenAddress", request.getTokenAddress());
        params.put("walletAddress", request.getWalletAddress());
        
        return httpClient.getAsync("/approve/allowance", params, AllowanceResponse.class);
    }
    
    private Map<String, Object> buildQuoteParams(QuoteRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("src", request.getSrc());
        params.put("dst", request.getDst());
        params.put("amount", request.getAmount());
        
        if (request.getProtocols() != null) params.put("protocols", request.getProtocols());
        if (request.getFee() != null) params.put("fee", request.getFee());
        if (request.getGasPrice() != null) params.put("gasPrice", request.getGasPrice());
        if (request.getComplexityLevel() != null) params.put("complexityLevel", request.getComplexityLevel());
        if (request.getParts() != null) params.put("parts", request.getParts());
        if (request.getMainRouteParts() != null) params.put("mainRouteParts", request.getMainRouteParts());
        if (request.getGasLimit() != null) params.put("gasLimit", request.getGasLimit());
        if (request.getIncludeTokensInfo() != null) params.put("includeTokensInfo", request.getIncludeTokensInfo());
        if (request.getIncludeProtocols() != null) params.put("includeProtocols", request.getIncludeProtocols());
        if (request.getIncludeGas() != null) params.put("includeGas", request.getIncludeGas());
        if (request.getConnectorTokens() != null) params.put("connectorTokens", request.getConnectorTokens());
        if (request.getExcludedProtocols() != null) params.put("excludedProtocols", request.getExcludedProtocols());
        
        return params;
    }
    
    private Map<String, Object> buildSwapParams(SwapRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("src", request.getSrc());
        params.put("dst", request.getDst());
        params.put("amount", request.getAmount());
        params.put("from", request.getFrom());
        params.put("origin", request.getOrigin());
        params.put("slippage", request.getSlippage());
        
        if (request.getProtocols() != null) params.put("protocols", request.getProtocols());
        if (request.getFee() != null) params.put("fee", request.getFee());
        if (request.getGasPrice() != null) params.put("gasPrice", request.getGasPrice());
        if (request.getComplexityLevel() != null) params.put("complexityLevel", request.getComplexityLevel());
        if (request.getParts() != null) params.put("parts", request.getParts());
        if (request.getMainRouteParts() != null) params.put("mainRouteParts", request.getMainRouteParts());
        if (request.getGasLimit() != null) params.put("gasLimit", request.getGasLimit());
        if (request.getIncludeTokensInfo() != null) params.put("includeTokensInfo", request.getIncludeTokensInfo());
        if (request.getIncludeProtocols() != null) params.put("includeProtocols", request.getIncludeProtocols());
        if (request.getIncludeGas() != null) params.put("includeGas", request.getIncludeGas());
        if (request.getConnectorTokens() != null) params.put("connectorTokens", request.getConnectorTokens());
        if (request.getExcludedProtocols() != null) params.put("excludedProtocols", request.getExcludedProtocols());
        if (request.getPermit() != null) params.put("permit", request.getPermit());
        if (request.getReceiver() != null) params.put("receiver", request.getReceiver());
        if (request.getReferrer() != null) params.put("referrer", request.getReferrer());
        if (request.getAllowPartialFill() != null) params.put("allowPartialFill", request.getAllowPartialFill());
        if (request.getDisableEstimate() != null) params.put("disableEstimate", request.getDisableEstimate());
        if (request.getUsePermit2() != null) params.put("usePermit2", request.getUsePermit2());
        
        return params;
    }
}