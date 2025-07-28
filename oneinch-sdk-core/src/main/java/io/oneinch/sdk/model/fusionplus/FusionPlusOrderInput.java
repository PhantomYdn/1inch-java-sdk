package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order input for FusionPlus cross-chain orders.
 * Enhanced order structure with cross-chain capabilities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FusionPlusOrderInput {

    /**
     * Unique salt for order identification
     */
    @JsonProperty("salt")
    private String salt;

    /**
     * Address of the order maker
     */
    @JsonProperty("maker")
    private String maker;

    /**
     * Address that will receive the filled order
     */
    @JsonProperty("receiver")
    private String receiver;

    /**
     * Address of the token being sold (maker asset)
     */
    @JsonProperty("makerAsset")
    private String makerAsset;

    /**
     * Address of the token being bought (taker asset)
     */
    @JsonProperty("takerAsset")
    private String takerAsset;

    /**
     * Amount of maker asset being sold
     */
    @JsonProperty("makingAmount")
    private String makingAmount;

    /**
     * Amount of taker asset being bought
     */
    @JsonProperty("takingAmount")
    private String takingAmount;

    /**
     * Maker traits and flags (encoded configuration)
     */
    @JsonProperty("makerTraits")
    private String makerTraits;

    /**
     * Source chain ID for cross-chain orders
     */
    @JsonProperty("srcChainId")
    private Integer srcChainId;

    /**
     * Destination chain ID for cross-chain orders
     */
    @JsonProperty("dstChainId")
    private Integer dstChainId;
}