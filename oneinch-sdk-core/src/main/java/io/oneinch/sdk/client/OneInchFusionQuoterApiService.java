package io.oneinch.sdk.client;

import io.oneinch.sdk.model.fusion.CustomPresetInput;
import io.oneinch.sdk.model.fusion.GetQuoteOutput;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

/**
 * Retrofit API service interface for 1inch Fusion Quoter API.
 * Handles quote generation with preset configurations and auction mechanisms.
 */
public interface OneInchFusionQuoterApiService {
    
    /**
     * Get quote details based on input data with default presets.
     *
     * @param chainId Chain ID for the network
     * @param fromTokenAddress Address of "FROM" token
     * @param toTokenAddress Address of "TO" token
     * @param amount Amount to take from "FROM" token to get "TO" token
     * @param walletAddress An address of the wallet or contract who will create Fusion order
     * @param enableEstimate If enabled then get estimation from 1inch swap builder and generates quoteId
     * @param fee Fee in bps format, 1% is equal to 100bps
     * @param showDestAmountMinusFee Show destination amount minus fee
     * @param isPermit2 Permit2 allowance transfer encoded call
     * @param surplus Enable surplus calculation
     * @param permit Permit, user approval sign
     * @param slippage Slippage configuration
     * @param source Source configuration
     * @return Single containing quote with presets and slippage
     */
    @GET("fusion/v2.0/{chainId}/quoter/quote/receive")
    Single<GetQuoteOutput> getQuote(
            @Path("chainId") Integer chainId,
            @Query("fromTokenAddress") String fromTokenAddress,
            @Query("toTokenAddress") String toTokenAddress,
            @Query("amount") String amount,
            @Query("walletAddress") String walletAddress,
            @Query("enableEstimate") Boolean enableEstimate,
            @Query("fee") Integer fee,
            @Query("showDestAmountMinusFee") Boolean showDestAmountMinusFee,
            @Query("isPermit2") String isPermit2,
            @Query("surplus") Boolean surplus,
            @Query("permit") String permit,
            @Query("slippage") Object slippage,
            @Query("source") Object source
    );
    
    /**
     * Get quote with custom preset details.
     *
     * @param chainId Chain ID for the network
     * @param fromTokenAddress Address of "FROM" token
     * @param toTokenAddress Address of "TO" token
     * @param amount Amount to take from "FROM" token to get "TO" token
     * @param walletAddress An address of the wallet or contract who will create Fusion order
     * @param enableEstimate If enabled then get estimation from 1inch swap builder and generates quoteId
     * @param fee Fee in bps format, 1% is equal to 100bps
     * @param showDestAmountMinusFee Show destination amount minus fee
     * @param isPermit2 Permit2 allowance transfer encoded call
     * @param surplus Enable surplus calculation
     * @param permit Permit, user approval sign
     * @param source Source configuration
     * @param customPreset Custom preset configuration
     * @return Single containing quote with custom preset details
     */
    @POST("fusion/v2.0/{chainId}/quoter/quote/receive")
    Single<GetQuoteOutput> getQuoteWithCustomPresets(
            @Path("chainId") Integer chainId,
            @Query("fromTokenAddress") String fromTokenAddress,
            @Query("toTokenAddress") String toTokenAddress,
            @Query("amount") String amount,
            @Query("walletAddress") String walletAddress,
            @Query("enableEstimate") Boolean enableEstimate,
            @Query("fee") Integer fee,
            @Query("showDestAmountMinusFee") Boolean showDestAmountMinusFee,
            @Query("isPermit2") String isPermit2,
            @Query("surplus") Boolean surplus,
            @Query("permit") String permit,
            @Query("source") Object source,
            @Body CustomPresetInput customPreset
    );
}