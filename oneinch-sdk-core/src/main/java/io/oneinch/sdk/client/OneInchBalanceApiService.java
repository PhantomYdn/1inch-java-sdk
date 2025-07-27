package io.oneinch.sdk.client;

import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * Retrofit interface for 1inch Balance API v1.2
 */
public interface OneInchBalanceApiService {

    /**
     * Get balances of tokens for walletAddress
     * GET /balance/v1.2/{chainId}/balances/{walletAddress}
     */
    @GET("balance/v1.2/{chainId}/balances/{walletAddress}")
    Single<Map<String, String>> getBalances(
            @Path("chainId") Integer chainId,
            @Path("walletAddress") String walletAddress
    );

    /**
     * Get balances of custom tokens for walletAddress
     * POST /balance/v1.2/{chainId}/balances/{walletAddress}
     */
    @POST("balance/v1.2/{chainId}/balances/{walletAddress}")
    Single<Map<String, String>> getCustomBalances(
            @Path("chainId") Integer chainId,
            @Path("walletAddress") String walletAddress,
            @Body CustomTokensBalanceRequest tokens
    );

    /**
     * Get allowances of tokens by spender for walletAddress
     * GET /balance/v1.2/{chainId}/allowances/{spender}/{walletAddress}
     */
    @GET("balance/v1.2/{chainId}/allowances/{spender}/{walletAddress}")
    Single<Map<String, String>> getAllowances(
            @Path("chainId") Integer chainId,
            @Path("spender") String spender,
            @Path("walletAddress") String walletAddress
    );

    /**
     * Get allowances of custom tokens by spender for walletAddress
     * POST /balance/v1.2/{chainId}/allowances/{spender}/{walletAddress}
     */
    @POST("balance/v1.2/{chainId}/allowances/{spender}/{walletAddress}")
    Single<Map<String, String>> getCustomAllowances(
            @Path("chainId") Integer chainId,
            @Path("spender") String spender,
            @Path("walletAddress") String walletAddress,
            @Body CustomTokensBalanceRequest tokens
    );

    /**
     * Get balances and allowances of tokens by spender for walletAddress
     * GET /balance/v1.2/{chainId}/allowancesAndBalances/{spender}/{walletAddress}
     */
    @GET("balance/v1.2/{chainId}/allowancesAndBalances/{spender}/{walletAddress}")
    Single<Map<String, BalanceAndAllowanceItem>> getAllowancesAndBalances(
            @Path("chainId") Integer chainId,
            @Path("spender") String spender,
            @Path("walletAddress") String walletAddress
    );

    /**
     * Get balances and allowances of custom tokens by spender for walletAddress
     * POST /balance/v1.2/{chainId}/allowancesAndBalances/{spender}/{walletAddress}
     */
    @POST("balance/v1.2/{chainId}/allowancesAndBalances/{spender}/{walletAddress}")
    Single<Map<String, BalanceAndAllowanceItem>> getCustomAllowancesAndBalances(
            @Path("chainId") Integer chainId,
            @Path("spender") String spender,
            @Path("walletAddress") String walletAddress,
            @Body CustomTokensBalanceRequest tokens
    );

    /**
     * Get balances and allowances by spender for list of wallets addresses
     * GET /balance/v1.2/{chainId}/aggregatedBalancesAndAllowances/{spender}
     */
    @GET("balance/v1.2/{chainId}/aggregatedBalancesAndAllowances/{spender}")
    Single<List<AggregatedBalanceResponse>> getAggregatedBalancesAndAllowances(
            @Path("chainId") Integer chainId,
            @Path("spender") String spender,
            @Query("wallets") List<String> wallets,
            @Query("filterEmpty") Boolean filterEmpty
    );

    /**
     * Get balances of custom tokens for list of wallets addresses
     * POST /balance/v1.2/{chainId}/balances/multiple/walletsAndTokens
     */
    @POST("balance/v1.2/{chainId}/balances/multiple/walletsAndTokens")
    Single<Map<String, Map<String, String>>> getBalancesByMultipleWallets(
            @Path("chainId") Integer chainId,
            @Body CustomTokensAndWalletsBalanceRequest request
    );
}