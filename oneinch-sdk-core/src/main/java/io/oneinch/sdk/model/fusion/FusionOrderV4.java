package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Core Fusion order structure according to the FusionOrderV4 specification.
 * Represents a gasless limit order that can be filled by professional market makers (resolvers).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FusionOrderV4 {
    
    /**
     * Some unique value. It is necessary to be able to create limit orders with the same parameters 
     * (so that they have a different hash). Lowest 160 bits of the order salt must be equal to 
     * the lowest 160 bits of the extension hash.
     */
    @JsonProperty("salt")
    private String salt;
    
    /**
     * Address of the account creating the order (maker).
     */
    @JsonProperty("maker")
    private String maker;
    
    /**
     * Address of the account receiving the assets (receiver), if different from maker.
     */
    @JsonProperty("receiver")
    private String receiver;
    
    /**
     * Identifier of the asset being offered by the maker.
     */
    @JsonProperty("makerAsset")
    private String makerAsset;
    
    /**
     * Identifier of the asset being requested by the maker in exchange.
     */
    @JsonProperty("takerAsset")
    private String takerAsset;
    
    /**
     * Amount of the makerAsset being offered by the maker.
     */
    @JsonProperty("makingAmount")
    private String makingAmount;
    
    /**
     * Amount of the takerAsset being requested by the maker.
     */
    @JsonProperty("takingAmount")
    private String takingAmount;
    
    /**
     * Includes some flags like, allow multiple fills, is partial fill allowed or not, 
     * price improvement, nonce, deadline etc.
     */
    @JsonProperty("makerTraits")
    private String makerTraits;
}