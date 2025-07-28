package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Custom preset parameters for FusionPlus quote requests.
 * Allows users to define custom auction and pricing strategies.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomPresetParams {

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
     * Final amount at auction end
     */
    @JsonProperty("auctionEndAmount")
    private String auctionEndAmount;

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
     * Number of secrets required for cross-chain execution
     */
    @JsonProperty("secretsCount")
    private Integer secretsCount;

    /**
     * Gas bump estimate for order execution
     */
    @JsonProperty("gasBumpEstimate")
    private Integer gasBumpEstimate;

    /**
     * Gas price estimate in wei
     */
    @JsonProperty("gasPriceEstimate")
    private String gasPriceEstimate;
}