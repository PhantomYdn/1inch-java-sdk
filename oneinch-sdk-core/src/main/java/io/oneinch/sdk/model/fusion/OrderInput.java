package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Core order data structure for order submission.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInput {
    
    /**
     * Unique salt value for the order.
     */
    @JsonProperty("salt")
    private String salt;
    
    /**
     * Address of the maker asset.
     */
    @JsonProperty("makerAsset")
    private String makerAsset;
    
    /**
     * Address of the taker asset.
     */
    @JsonProperty("takerAsset")
    private String takerAsset;
    
    /**
     * An address of the maker (wallet or contract address).
     */
    @JsonProperty("maker")
    private String maker;
    
    /**
     * An address of the wallet or contract who will receive filled amount.
     * Default: 0x0000000000000000000000000000000000000000
     */
    @JsonProperty("receiver")
    private String receiver;
    
    /**
     * Order maker's token amount.
     */
    @JsonProperty("makingAmount")
    private String makingAmount;
    
    /**
     * Order taker's token amount.
     */
    @JsonProperty("takingAmount")
    private String takingAmount;
    
    /**
     * Includes some flags like, allow multiple fills, is partial fill allowed or not, 
     * price improvement, nonce, deadline etc.
     * Default: "0"
     */
    @JsonProperty("makerTraits")
    private String makerTraits;
}