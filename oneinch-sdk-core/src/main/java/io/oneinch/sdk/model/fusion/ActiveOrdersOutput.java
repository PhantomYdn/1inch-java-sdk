package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Represents an active Fusion order with its current state and auction details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveOrdersOutput {
    
    /**
     * Order hash identifier.
     * Example: 0x806039f5149065924ad52de616b50abff488c986716d052e9c160887bc09e559
     */
    @JsonProperty("orderHash")
    private String orderHash;
    
    /**
     * Signature of the order.
     * Example: 0x38de7c8c406c8668eec947d59679028c068735e56c8a41bcc5b3dc2d2229dec258424e0f06b189d2b87f9f3d9cdd9edcb7b3be4108bd8605d052c20c84e65ad61c
     */
    @JsonProperty("signature")
    private String signature;
    
    /**
     * Deadline by which the order must be filled.
     */
    @JsonProperty("deadline")
    private OffsetDateTime deadline;
    
    /**
     * Start date of the auction for this order.
     */
    @JsonProperty("auctionStartDate")
    private OffsetDateTime auctionStartDate;
    
    /**
     * End date of the auction for this order.
     */
    @JsonProperty("auctionEndDate")
    private OffsetDateTime auctionEndDate;
    
    /**
     * Identifier of the quote associated with this order.
     */
    @JsonProperty("quoteId")
    private String quoteId;
    
    /**
     * Remaining amount of the maker asset that can still be filled.
     */
    @JsonProperty("remainingMakerAmount")
    private String remainingMakerAmount;
    
    /**
     * An interaction call data. ABI encoded set of makerAssetSuffix, takerAssetSuffix, 
     * makingAmountGetter, takingAmountGetter, predicate, permit, preInteraction, postInteraction.
     * If extension exists then lowest 160 bits of the order salt must be equal to 
     * the lowest 160 bits of the extension hash.
     */
    @JsonProperty("extension")
    private String extension;
    
    /**
     * Detailed structure of the limit order according to the FusionOrderV4 specification.
     */
    @JsonProperty("order")
    private FusionOrderV4 order;
    
    /**
     * Version of settlement contract. Supported: 2.0, 2.1, and 2.2
     */
    @JsonProperty("version")
    private String version;
}