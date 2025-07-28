package io.oneinch.sdk.client;

import io.oneinch.sdk.model.fusionplus.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

/**
 * Retrofit API service interface for 1inch FusionPlus Quoter API.
 * Provides access to cross-chain price curves and quote generation.
 */
public interface OneInchFusionPlusQuoterApiService {
    
    /**
     * Get cross-chain quote details based on input data.
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param srcTokenAddress Address of source token in source chain
     * @param dstTokenAddress Address of destination token in destination chain
     * @param amount Amount to take from source token
     * @param walletAddress Address of wallet that will create Fusion order
     * @param enableEstimate Enable estimation from 1inch swap builder
     * @param fee Fee in basis points (optional)
     * @param isPermit2 Permit2 allowance transfer encoded call (optional)
     * @param permit Permit user approval signature (optional)
     * @return Single containing cross-chain quote details
     */
    @GET("quoter/v1.0/quote/receive")
    Single<GetQuoteOutput> getQuote(
            @Query("srcChain") Integer srcChain,
            @Query("dstChain") Integer dstChain,
            @Query("srcTokenAddress") String srcTokenAddress,
            @Query("dstTokenAddress") String dstTokenAddress,
            @Query("amount") String amount,
            @Query("walletAddress") String walletAddress,
            @Query("enableEstimate") Boolean enableEstimate,
            @Query("fee") Integer fee,
            @Query("isPermit2") String isPermit2,
            @Query("permit") String permit
    );
    
    /**
     * Get cross-chain quote with custom preset details.
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param srcTokenAddress Address of source token
     * @param dstTokenAddress Address of destination token
     * @param amount Amount to take from source token
     * @param walletAddress Address of wallet that will create Fusion order
     * @param enableEstimate Enable estimation from 1inch swap builder
     * @param fee Fee in basis points (optional)
     * @param isPermit2 Permit2 allowance transfer encoded call (optional)
     * @param permit Permit user approval signature (optional)
     * @param customPresetParams Custom preset configuration
     * @return Single containing quote with custom preset details
     */
    @POST("quoter/v1.0/quote/receive")
    Single<GetQuoteOutput> getQuoteWithCustomPresets(
            @Query("srcChain") Integer srcChain,
            @Query("dstChain") Integer dstChain,
            @Query("srcTokenAddress") String srcTokenAddress,
            @Query("dstTokenAddress") String dstTokenAddress,
            @Query("amount") String amount,
            @Query("walletAddress") String walletAddress,
            @Query("enableEstimate") Boolean enableEstimate,
            @Query("fee") Integer fee,
            @Query("isPermit2") String isPermit2,
            @Query("permit") String permit,
            @Body CustomPresetParams customPresetParams
    );
    
    /**
     * Build cross-chain order by given quote.
     *
     * @param srcChain Source chain ID
     * @param dstChain Destination chain ID
     * @param srcTokenAddress Address of source token
     * @param dstTokenAddress Address of destination token
     * @param amount Amount to take from source token
     * @param walletAddress Address of wallet that will create Fusion order
     * @param fee Fee in basis points (optional)
     * @param source Frontend or source selector (optional)
     * @param isPermit2 Permit2 allowance transfer encoded call (optional)
     * @param isMobile Mobile flag for history tracking (optional)
     * @param feeReceiver Fee receiver address if fee is non-zero (optional)
     * @param permit Permit user approval signature (optional)
     * @param preset Preset type: fast/medium/slow/custom (optional)
     * @param buildOrderBody Request body containing quote and secret hashes
     * @return Single containing cross-chain order details with EIP712 typed data
     */
    @POST("quoter/v1.0/quote/build")
    Single<BuildOrderOutput> buildQuoteTypedData(
            @Query("srcChain") Integer srcChain,
            @Query("dstChain") Integer dstChain,
            @Query("srcTokenAddress") String srcTokenAddress,
            @Query("dstTokenAddress") String dstTokenAddress,
            @Query("amount") String amount,
            @Query("walletAddress") String walletAddress,
            @Query("fee") Integer fee,
            @Query("source") String source,
            @Query("isPermit2") String isPermit2,
            @Query("isMobile") String isMobile,
            @Query("feeReceiver") String feeReceiver,
            @Query("permit") String permit,
            @Query("preset") String preset,
            @Body BuildOrderBody buildOrderBody
    );
}