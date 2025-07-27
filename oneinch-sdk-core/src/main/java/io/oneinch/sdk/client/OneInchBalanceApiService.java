package io.oneinch.sdk.client;

import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Retrofit interface for 1inch Balance API v1.2
 */
public interface OneInchBalanceApiService {

    /**
     * Get balances of all tokens for a wallet address.
     * Returns token balances for all detected tokens in the wallet.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param walletAddress wallet address to check balances for
     * @return Single containing map of token address to balance amount in wei
     */
    @GET("balance/v1.2/{chainId}/balances/{walletAddress}")
    Single<Map<String, BigInteger>> getBalances(
            @Path("chainId") Integer chainId,
            @Path("walletAddress") String walletAddress
    );

    /**
     * Get balances of specific custom tokens for a wallet address.
     * Returns token balances only for the specified token addresses.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param walletAddress wallet address to check balances for
     * @param tokens request body containing list of custom token addresses to check
     * @return Single containing map of token address to balance amount in wei
     */
    @POST("balance/v1.2/{chainId}/balances/{walletAddress}")
    Single<Map<String, BigInteger>> getCustomBalances(
            @Path("chainId") Integer chainId,
            @Path("walletAddress") String walletAddress,
            @Body CustomTokensBalanceRequest tokens
    );

    /**
     * Get token allowances approved for a spender by a wallet.
     * Returns how much each token the spender is allowed to spend.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param spender spender address (e.g., DEX router contract)
     * @param walletAddress wallet address that owns the tokens
     * @return Single containing map of token address to allowance amount in wei
     */
    @GET("balance/v1.2/{chainId}/allowances/{spender}/{walletAddress}")
    Single<Map<String, BigInteger>> getAllowances(
            @Path("chainId") Integer chainId,
            @Path("spender") String spender,
            @Path("walletAddress") String walletAddress
    );

    /**
     * Get allowances of specific custom tokens approved for a spender.
     * Returns allowances only for the specified token addresses.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param spender spender address (e.g., DEX router contract)
     * @param walletAddress wallet address that owns the tokens
     * @param tokens request body containing list of custom token addresses to check
     * @return Single containing map of token address to allowance amount in wei
     */
    @POST("balance/v1.2/{chainId}/allowances/{spender}/{walletAddress}")
    Single<Map<String, BigInteger>> getCustomAllowances(
            @Path("chainId") Integer chainId,
            @Path("spender") String spender,
            @Path("walletAddress") String walletAddress,
            @Body CustomTokensBalanceRequest tokens
    );

    /**
     * Get both balances and allowances of tokens in a single request.
     * Returns combined balance and allowance data for efficiency.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param spender spender address (e.g., DEX router contract)
     * @param walletAddress wallet address to check balances and allowances for
     * @return Single containing map of token address to balance and allowance item
     */
    @GET("balance/v1.2/{chainId}/allowancesAndBalances/{spender}/{walletAddress}")
    Single<Map<String, BalanceAndAllowanceItem>> getAllowancesAndBalances(
            @Path("chainId") Integer chainId,
            @Path("spender") String spender,
            @Path("walletAddress") String walletAddress
    );

    /**
     * Get both balances and allowances of specific custom tokens.
     * Returns combined data only for the specified token addresses.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param spender spender address (e.g., DEX router contract)
     * @param walletAddress wallet address to check balances and allowances for
     * @param tokens request body containing list of custom token addresses to check
     * @return Single containing map of token address to balance and allowance item
     */
    @POST("balance/v1.2/{chainId}/allowancesAndBalances/{spender}/{walletAddress}")
    Single<Map<String, BalanceAndAllowanceItem>> getCustomAllowancesAndBalances(
            @Path("chainId") Integer chainId,
            @Path("spender") String spender,
            @Path("walletAddress") String walletAddress,
            @Body CustomTokensBalanceRequest tokens
    );

    /**
     * Get aggregated balances and allowances for multiple wallets.
     * Returns combined data across multiple wallet addresses for portfolio analysis.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param spender spender address (e.g., DEX router contract)
     * @param wallets list of wallet addresses to aggregate data for
     * @param filterEmpty whether to filter out tokens with zero balances (optional)
     * @return Single containing list of aggregated balance responses with token metadata
     */
    @GET("balance/v1.2/{chainId}/aggregatedBalancesAndAllowances/{spender}")
    Single<List<AggregatedBalanceResponse>> getAggregatedBalancesAndAllowances(
            @Path("chainId") Integer chainId,
            @Path("spender") String spender,
            @Query("wallets") List<String> wallets,
            @Query("filterEmpty") Boolean filterEmpty
    );

    /**
     * Get balances of specific tokens across multiple wallets.
     * Returns token balances organized by wallet address for bulk queries.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param request request body containing lists of wallet addresses and token addresses
     * @return Single containing nested map of wallet address to token address to balance amount
     */
    @POST("balance/v1.2/{chainId}/balances/multiple/walletsAndTokens")
    Single<Map<String, Map<String, BigInteger>>> getBalancesByMultipleWallets(
            @Path("chainId") Integer chainId,
            @Body CustomTokensAndWalletsBalanceRequest request
    );
}