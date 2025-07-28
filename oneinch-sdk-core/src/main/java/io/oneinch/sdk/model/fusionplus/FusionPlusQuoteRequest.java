package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Quote request for FusionPlus cross-chain swaps.
 * Enhanced with source and destination chain parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FusionPlusQuoteRequest {

    /**
     * Source chain ID where the order originates
     */
    @JsonProperty("srcChain")
    private Integer srcChain;

    /**
     * Destination chain ID where the order will be fulfilled
     */
    @JsonProperty("dstChain")
    private Integer dstChain;

    /**
     * Address of the source token in source chain
     */
    @JsonProperty("srcTokenAddress")
    private String srcTokenAddress;

    /**
     * Address of the destination token in destination chain
     */
    @JsonProperty("dstTokenAddress")
    private String dstTokenAddress;

    /**
     * Amount to take from source token (in wei)
     */
    @JsonProperty("amount")
    private String amount;

    /**
     * Wallet address that will create the Fusion order
     */
    @JsonProperty("walletAddress")
    private String walletAddress;

    /**
     * Enable estimation from 1inch swap builder
     */
    @JsonProperty("enableEstimate")
    private Boolean enableEstimate;

    /**
     * Fee in basis points (100 bps = 1%)
     */
    @JsonProperty("fee")
    private Integer fee;

    /**
     * Permit2 allowance transfer encoded call
     */
    @JsonProperty("isPermit2")
    private String isPermit2;

    /**
     * Permit user approval signature
     */
    @JsonProperty("permit")
    private String permit;
}