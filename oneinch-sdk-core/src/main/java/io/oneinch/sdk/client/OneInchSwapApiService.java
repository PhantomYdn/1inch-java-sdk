package io.oneinch.sdk.client;

import io.oneinch.sdk.model.swap.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.math.BigInteger;

/**
 * Retrofit interface for 1inch Swap API v6.1
 * Provides token swapping, quoting, and approval functionality
 */
public interface OneInchSwapApiService {
    
    /**
     * Get the best quote for token swap without executing the transaction.
     * Returns routing information and estimated output amount.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param src source token contract address
     * @param dst destination token contract address
     * @param amount amount of source token to swap (in wei)
     * @param protocols comma-separated list of protocols to use (optional)
     * @param fee fee percentage in basis points (optional)
     * @param gasPrice gas price in wei (optional)
     * @param complexityLevel routing complexity level (optional)
     * @param parts number of split parts for routing (optional)
     * @param mainRouteParts number of main route parts (optional)
     * @param gasLimit gas limit for the transaction (optional)
     * @param includeTokensInfo whether to include token information in response (optional)
     * @param includeProtocols whether to include protocol information in response (optional)
     * @param includeGas whether to include gas estimation in response (optional)
     * @param connectorTokens comma-separated list of connector token addresses (optional)
     * @param excludedProtocols comma-separated list of protocols to exclude (optional)
     * @return Single containing quote response with routing and estimated output
     */
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

    /**
     * Generate transaction calldata for token swap execution.
     * Returns calldata that can be submitted to perform the actual swap.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param src source token contract address
     * @param dst destination token contract address
     * @param amount amount of source token to swap (in wei)
     * @param from wallet address that will execute the swap
     * @param origin transaction origin address (optional)
     * @param slippage maximum acceptable slippage percentage (e.g., 1.0 for 1%)
     * @param protocols comma-separated list of protocols to use (optional)
     * @param fee fee percentage in basis points (optional)
     * @param gasPrice gas price in wei (optional)
     * @param complexityLevel routing complexity level (optional)
     * @param parts number of split parts for routing (optional)
     * @param mainRouteParts number of main route parts (optional)
     * @param gasLimit gas limit for the transaction (optional)
     * @param includeTokensInfo whether to include token information in response (optional)
     * @param includeProtocols whether to include protocol information in response (optional)
     * @param includeGas whether to include gas estimation in response (optional)
     * @param connectorTokens comma-separated list of connector token addresses (optional)
     * @param excludedProtocols comma-separated list of protocols to exclude (optional)
     * @param permit permit signature for gasless approvals (optional)
     * @param receiver address that will receive the destination tokens (optional)
     * @param referrer referrer address for fee sharing (optional)
     * @param allowPartialFill whether to allow partial fills (optional)
     * @param disableEstimate whether to disable gas estimation (optional)
     * @param usePermit2 whether to use Permit2 for approvals (optional)
     * @return Single containing swap response with transaction calldata
     */
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
    
    /**
     * Get the 1inch router contract address for token approvals.
     * Returns the address that tokens should be approved for before swapping.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @return Single containing spender response with router contract address
     */
    @GET("swap/v6.1/{chain}/approve/spender")
    Single<SpenderResponse> getSpender(
        @Path("chain") Integer chainId
    );
    
    /**
     * Generate transaction calldata for token approval.
     * Returns calldata to approve the 1inch router to spend tokens.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param tokenAddress address of the token contract to approve
     * @param amount amount of tokens to approve for spending (in wei)
     * @return Single containing approve calldata response with transaction data
     */
    @GET("swap/v6.1/{chain}/approve/transaction")
    Single<ApproveCallDataResponse> getApproveTransaction(
            @Path("chain") Integer chainId,
            @Query("tokenAddress") String tokenAddress,
            @Query("amount") BigInteger amount
    );

    /**
     * Check the current token allowance for the 1inch router.
     * Returns how much of a token the router is allowed to spend.
     *
     * @param chainId blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     * @param tokenAddress address of the token contract to check
     * @param walletAddress wallet address that owns the tokens
     * @return Single containing allowance response with current approval amount
     */
    @GET("swap/v6.1/{chain}/approve/allowance")
    Single<AllowanceResponse> getAllowance(
            @Path("chain") Integer chainId,
            @Query("tokenAddress") String tokenAddress,
            @Query("walletAddress") String walletAddress
    );
}