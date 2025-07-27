package io.oneinch.sdk.client;

import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.math.BigInteger;

public interface OneInchSwapApiService {
    
    @GET("swap/v6.1/{chain}/quote")
    Single<QuoteResponse> getQuote(
            @Path("chain") Integer chainId,
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

    @GET("swap/v6.1/{chain}/swap")
    Single<SwapResponse> getSwap(
            @Path("chain") Integer chainId,
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
    
    @GET("swap/v6.1/{chain}/approve/spender")
    Single<SpenderResponse> getSpender(
        @Path("chain") Integer chainId
    );
    
    @GET("swap/v6.1/{chain}/approve/transaction")
    Single<ApproveCallDataResponse> getApproveTransaction(
            @Path("chain") Integer chainId,
            @Query("tokenAddress") String tokenAddress,
            @Query("amount") BigInteger amount
    );

    @GET("swap/v6.1/{chain}/approve/allowance")
    Single<AllowanceResponse> getAllowance(
            @Path("chain") Integer chainId,
            @Query("tokenAddress") String tokenAddress,
            @Query("walletAddress") String walletAddress
    );
}