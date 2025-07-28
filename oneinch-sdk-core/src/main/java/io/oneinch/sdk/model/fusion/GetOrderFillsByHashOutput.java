package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response containing order status and fill details by order hash.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderFillsByHashOutput {
    
    /**
     * Order hash.
     * Example: 0x496755a88564d8ded6759dff0252d3e6c3ef1fe42b4fa1bbc3f03bd2674f1078
     */
    @JsonProperty("orderHash")
    private String orderHash;
    
    /**
     * Current order status.
     */
    @JsonProperty("status")
    private OrderStatus status;
    
    /**
     * Order data structure.
     */
    @JsonProperty("order")
    private LimitOrderV4StructOutput order;
    
    /**
     * An interaction call data. ABI encoded set of makerAssetSuffix, takerAssetSuffix, 
     * makingAmountGetter, takingAmountGetter, predicate, permit, preInteraction, postInteraction.
     * If extension exists then lowest 160 bits of the order salt must be equal to 
     * the lowest 160 bits of the extension hash.
     */
    @JsonProperty("extension")
    private String extension;
    
    /**
     * Auction pricing curve points (nullable).
     */
    @JsonProperty("points")
    private List<AuctionPointOutput> points;
    
    /**
     * Approximate amount of the takerAsset being requested by the maker in dst chain.
     * Example: 100000000000000000
     */
    @JsonProperty("approximateTakingAmount")
    private String approximateTakingAmount;
    
    /**
     * List of order fills/executions.
     */
    @JsonProperty("fills")
    private List<FillsOutput> fills;
    
    /**
     * Unix timestamp in milliseconds when auction started.
     * Example: 123123123123
     */
    @JsonProperty("auctionStartDate")
    private Long auctionStartDate;
    
    /**
     * Auction duration in milliseconds.
     * Example: 123123123123
     */
    @JsonProperty("auctionDuration")
    private Long auctionDuration;
    
    /**
     * Initial rate bump.
     * Example: 1000
     */
    @JsonProperty("initialRateBump")
    private Integer initialRateBump;
    
    /**
     * Shows if user received more than expected.
     * Example: 1000
     */
    @JsonProperty("positiveSurplus")
    private String positiveSurplus;
    
    /**
     * Is native currency or not.
     */
    @JsonProperty("isNativeCurrency")
    private Boolean isNativeCurrency;
    
    /**
     * Version of settlement contract. Supported: 2.0, 2.1, and 2.2
     */
    @JsonProperty("version")
    private String version;
}