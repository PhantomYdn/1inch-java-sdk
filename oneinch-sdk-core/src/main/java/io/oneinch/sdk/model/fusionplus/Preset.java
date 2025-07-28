package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Auction preset configuration for FusionPlus orders.
 * Defines auction parameters and pricing strategy.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Preset {

    /**
     * Duration of the auction in seconds
     */
    @JsonProperty("auctionDuration")
    private Integer auctionDuration;

    /**
     * Delay before auction starts in seconds
     */
    @JsonProperty("startAuctionIn")
    private Integer startAuctionIn;

    /**
     * Initial rate bump percentage (basis points)
     */
    @JsonProperty("initialRateBump")
    private Integer initialRateBump;

    /**
     * Starting amount for auction (string for precision)
     */
    @JsonProperty("auctionStartAmount")
    private String auctionStartAmount;

    /**
     * Start amount taking gas bump into account
     */
    @JsonProperty("startAmount")
    private String startAmount;

    /**
     * Final amount at auction end
     */
    @JsonProperty("auctionEndAmount")
    private String auctionEndAmount;

    /**
     * Exclusive resolver configuration (if any)
     */
    @JsonProperty("exclusiveResolver")
    private Object exclusiveResolver;

    /**
     * Cost calculation in destination token
     */
    @JsonProperty("costInDstToken")
    private String costInDstToken;

    /**
     * List of auction points defining price curve
     */
    @JsonProperty("points")
    private List<AuctionPoint> points;

    /**
     * Whether partial fills are allowed
     */
    @JsonProperty("allowPartialFills")
    private Boolean allowPartialFills;

    /**
     * Whether multiple fills are allowed
     */
    @JsonProperty("allowMultipleFills")
    private Boolean allowMultipleFills;

    /**
     * Gas cost configuration
     */
    @JsonProperty("gasCost")
    private GasCostConfig gasCost;

    /**
     * Number of secrets required for cross-chain execution
     */
    @JsonProperty("secretsCount")
    private Integer secretsCount;
}