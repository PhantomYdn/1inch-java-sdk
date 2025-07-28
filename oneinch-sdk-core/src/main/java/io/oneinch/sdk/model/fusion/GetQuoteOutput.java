package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response containing Fusion quote details with preset configurations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetQuoteOutput {
    
    /**
     * Current generated quote id, should be passed with order submission.
     */
    @JsonProperty("quoteId")
    private String quoteId;
    
    /**
     * Amount of the from token.
     */
    @JsonProperty("fromTokenAmount")
    private String fromTokenAmount;
    
    /**
     * Amount of the to token.
     */
    @JsonProperty("toTokenAmount")
    private String toTokenAmount;
    
    /**
     * Destination token address for fees.
     */
    @JsonProperty("feeToken")
    private String feeToken;
    
    /**
     * Resolver fee configuration.
     */
    @JsonProperty("fee")
    private ResolverFee fee;
    
    /**
     * Integrator fee in basis points.
     */
    @JsonProperty("integratorFee")
    private Integer integratorFee;
    
    /**
     * Various preset types which user can choose when using Fusion.
     */
    @JsonProperty("presets")
    private QuotePresetsClass presets;
    
    /**
     * Settlement contract address.
     */
    @JsonProperty("settlementAddress")
    private String settlementAddress;
    
    /**
     * Current executors whitelist addresses.
     */
    @JsonProperty("whitelist")
    private List<String> whitelist;
    
    /**
     * Suggested preset type to use.
     */
    @JsonProperty("recommended_preset")
    private PresetType recommendedPreset;
    
    /**
     * Whether it is suggested to use Fusion for this swap.
     */
    @JsonProperty("suggested")
    private Boolean suggested;
    
    /**
     * Token pair pricing information.
     */
    @JsonProperty("prices")
    private TokenPairValue prices;
    
    /**
     * Token pair volume information.
     */
    @JsonProperty("volume")
    private TokenPairValue volume;
    
    /**
     * Surplus fee in percent.
     */
    @JsonProperty("surplusFee")
    private Double surplusFee;
}