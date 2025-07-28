package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a Fusion order preset configuration (fast, medium, slow, or custom).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresetClass {
    
    /**
     * Bank fee for the preset.
     */
    @JsonProperty("bankFee")
    private String bankFee;
    
    /**
     * Duration of the auction in seconds.
     */
    @JsonProperty("auctionDuration")
    private Integer auctionDuration;
    
    /**
     * Delay before starting the auction in seconds.
     */
    @JsonProperty("startAuctionIn")
    private Integer startAuctionIn;
    
    /**
     * Initial rate bump percentage.
     */
    @JsonProperty("initialRateBump")
    private Integer initialRateBump;
    
    /**
     * Auction start amount.
     */
    @JsonProperty("auctionStartAmount")
    private String auctionStartAmount;
    
    /**
     * Auction start amount taking into account gas bump.
     */
    @JsonProperty("startAmount")
    private String startAmount;
    
    /**
     * Auction end amount.
     */
    @JsonProperty("auctionEndAmount")
    private String auctionEndAmount;
    
    /**
     * Exclusive resolver configuration (if any).
     */
    @JsonProperty("exclusiveResolver")
    private Object exclusiveResolver;
    
    /**
     * Token fee for the preset.
     */
    @JsonProperty("tokenFee")
    private String tokenFee;
    
    /**
     * Estimated probability parameter.
     */
    @JsonProperty("estP")
    private Double estP;
    
    /**
     * List of auction pricing points.
     */
    @JsonProperty("points")
    private List<AuctionPointClass> points;
    
    /**
     * Whether partial fills are allowed.
     */
    @JsonProperty("allowPartialFills")
    private Boolean allowPartialFills;
    
    /**
     * Whether multiple fills are allowed.
     */
    @JsonProperty("allowMultipleFills")
    private Boolean allowMultipleFills;
    
    /**
     * Gas cost configuration.
     */
    @JsonProperty("gasCost")
    private GasCostConfigClass gasCost;
}