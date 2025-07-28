package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Structure representing a limit order for output in order status responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitOrderV4StructOutput {
    
    @JsonProperty("salt")
    private String salt;
    
    /**
     * Maker address.
     * Example: 0x995BE1CA945174D5bA75410C1E658a41eB13a2FA
     */
    @JsonProperty("maker")
    private String maker;
    
    /**
     * Receiver address.
     * Example: 0x995BE1CA945174D5bA75410C1E658a41eB13a2FA
     */
    @JsonProperty("receiver")
    private String receiver;
    
    /**
     * Maker asset address.
     * Example: 0x995BE1CA945174D5bA75410C1E658a41eB13a2FA
     */
    @JsonProperty("makerAsset")
    private String makerAsset;
    
    /**
     * Taker asset address.
     * Example: 0x995BE1CA945174D5bA75410C1E658a41eB13a2FA
     */
    @JsonProperty("takerAsset")
    private String takerAsset;
    
    /**
     * Amount of the maker asset.
     * Example: 100000000000000000
     */
    @JsonProperty("makingAmount")
    private String makingAmount;
    
    /**
     * Amount of the taker asset.
     * Example: 100000000000000000
     */
    @JsonProperty("takingAmount")
    private String takingAmount;
    
    @JsonProperty("makerTraits")
    private String makerTraits;
}