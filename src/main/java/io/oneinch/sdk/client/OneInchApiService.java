package io.oneinch.sdk.client;

import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.math.BigInteger;

public interface OneInchApiService {
    
    @GET("quote")
    Single<QuoteResponse> getQuote(
            @Query("src") String src,
            @Query("dst") String dst,
            @Query("amount") BigInteger amount,
            @Query("protocols") String protocols,
            @Query("fee") Double fee,
            @Query("gasPrice") BigInteger gasPrice,
            @Query("complexityLevel") Integer complexityLevel,
            @Query("parts") Integer parts,
            @Query("mainRouteParts") Integer mainRouteParts,
            @Query("gasLimit") BigInteger gasLimit,
            @Query("includeTokensInfo") Boolean includeTokensInfo,
            @Query("includeProtocols") Boolean includeProtocols,
            @Query("includeGas") Boolean includeGas,
            @Query("connectorTokens") String connectorTokens,
            @Query("excludedProtocols") String excludedProtocols
    );
    
    @GET("swap")
    Single<SwapResponse> getSwap(
            @Query("src") String src,
            @Query("dst") String dst,
            @Query("amount") BigInteger amount,
            @Query("from") String from,
            @Query("origin") String origin,
            @Query("slippage") Double slippage,
            @Query("protocols") String protocols,
            @Query("fee") Double fee,
            @Query("gasPrice") BigInteger gasPrice,
            @Query("complexityLevel") Integer complexityLevel,
            @Query("parts") Integer parts,
            @Query("mainRouteParts") Integer mainRouteParts,
            @Query("gasLimit") BigInteger gasLimit,
            @Query("includeTokensInfo") Boolean includeTokensInfo,
            @Query("includeProtocols") Boolean includeProtocols,
            @Query("includeGas") Boolean includeGas,
            @Query("connectorTokens") String connectorTokens,
            @Query("excludedProtocols") String excludedProtocols,
            @Query("permit") String permit,
            @Query("receiver") String receiver,
            @Query("referrer") String referrer,
            @Query("allowPartialFill") Boolean allowPartialFill,
            @Query("disableEstimate") Boolean disableEstimate,
            @Query("usePermit2") Boolean usePermit2
    );
    
    @GET("approve/spender")
    Single<SpenderResponse> getSpender();
    
    @GET("approve/transaction")
    Single<ApproveCallDataResponse> getApproveTransaction(
            @Query("tokenAddress") String tokenAddress,
            @Query("amount") BigInteger amount
    );
    
    @GET("approve/allowance")
    Single<AllowanceResponse> getAllowance(
            @Query("tokenAddress") String tokenAddress,
            @Query("walletAddress") String walletAddress
    );
}