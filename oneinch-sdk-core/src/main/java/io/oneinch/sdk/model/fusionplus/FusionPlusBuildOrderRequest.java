package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Build order request for FusionPlus cross-chain orders.
 * Contains all necessary parameters for creating EIP712 typed data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FusionPlusBuildOrderRequest {

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
     * Address of the source token
     */
    @JsonProperty("srcTokenAddress")
    private String srcTokenAddress;

    /**
     * Address of the destination token
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
     * Fee in basis points (100 bps = 1%)
     */
    @JsonProperty("fee")
    private Integer fee;

    /**
     * Frontend or source selector
     */
    @JsonProperty("source")
    private String source;

    /**
     * Permit2 allowance transfer encoded call
     */
    @JsonProperty("isPermit2")
    private String isPermit2;

    /**
     * Mobile flag for history tracking
     */
    @JsonProperty("isMobile")
    private String isMobile;

    /**
     * Fee receiver address if fee is non-zero
     */
    @JsonProperty("feeReceiver")
    private String feeReceiver;

    /**
     * Permit user approval signature
     */
    @JsonProperty("permit")
    private String permit;

    /**
     * Preset type (fast/medium/slow/custom)
     */
    @JsonProperty("preset")
    private String preset;
}