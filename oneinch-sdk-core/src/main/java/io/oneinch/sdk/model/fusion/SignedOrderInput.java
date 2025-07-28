package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Signed order input for order submission to the relayer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignedOrderInput {
    
    /**
     * Gasless order data.
     */
    @JsonProperty("order")
    private OrderInput order;
    
    /**
     * Signature of the gasless order typed data (using signTypedData_v4).
     */
    @JsonProperty("signature")
    private String signature;
    
    /**
     * An interaction call data. ABI encoded set of makerAssetSuffix, takerAssetSuffix, 
     * makingAmountGetter, takingAmountGetter, predicate, permit, preInteraction, postInteraction.
     * Lowest 160 bits of the order salt must be equal to the lowest 160 bits of the extension hash.
     * Default: "0x"
     */
    @JsonProperty("extension")
    private String extension;
    
    /**
     * Quote id of the quote with presets.
     */
    @JsonProperty("quoteId")
    private String quoteId;
}