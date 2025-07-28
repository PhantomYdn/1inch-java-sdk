package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response containing order history and fills for a specific maker address.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFillsByMakerOutput {
    
    /**
     * Receiver address, it can be zero address or maker address.
     */
    @JsonProperty("receiver")
    private String receiver;
    
    /**
     * Order hash.
     * Example: 0x806039f5149065924ad52de616b50abff488c986716d052e9c160887bc09e559
     */
    @JsonProperty("orderHash")
    private String orderHash;
    
    /**
     * Current order status.
     */
    @JsonProperty("status")
    private OrderStatus status;
    
    /**
     * Identifier of the asset being offered by the maker.
     * Example: 0x1234567890abcdef1234567890abcdef12345678
     */
    @JsonProperty("makerAsset")
    private String makerAsset;
    
    /**
     * Amount of the makerAsset being offered by the maker.
     * Example: 1000000000000000000
     */
    @JsonProperty("makerAmount")
    private String makerAmount;
    
    /**
     * Minimum amount of the takerAsset being requested by the maker.
     * Example: 100000000000000000
     */
    @JsonProperty("minTakerAmount")
    private String minTakerAmount;
    
    /**
     * Approximate amount of the takerAsset being requested by the maker.
     * Example: 100000000000000000
     */
    @JsonProperty("approximateTakingAmount")
    private String approximateTakingAmount;
    
    /**
     * Shows if user received more than expected.
     * Example: 1000
     */
    @JsonProperty("positiveSurplus")
    private String positiveSurplus;
    
    /**
     * Identifier of the asset being requested by the maker in exchange.
     * Example: 0x1234567890abcdef1234567890abcdef12345678
     */
    @JsonProperty("takerAsset")
    private String takerAsset;
    
    /**
     * Transaction hash for order cancellation (if applicable).
     * Example: 0xa2768a9826b2c45a6937010ce21a91b1da9f8c7aa5194f68aa99306b22518b41
     */
    @JsonProperty("cancelTx")
    private String cancelTx;
    
    /**
     * List of order fills/executions.
     */
    @JsonProperty("fills")
    private List<FillOutputDto> fills;
    
    /**
     * Auction pricing curve points.
     */
    @JsonProperty("points")
    private List<AuctionPointOutput> points;
    
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
     * Is native currency or not.
     */
    @JsonProperty("isNativeCurrency")
    private Boolean isNativeCurrency;
    
    /**
     * Version of settlement contract. Supported: 2.0, 2.1, and 2.2
     */
    @JsonProperty("version")
    private String version;
    
    @JsonProperty("makerTraits")
    private String makerTraits;
}