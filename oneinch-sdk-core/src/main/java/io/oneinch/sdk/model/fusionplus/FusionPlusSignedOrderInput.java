package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Signed order input for FusionPlus cross-chain order submission.
 * Enhanced version supporting cross-chain parameters and secret management.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FusionPlusSignedOrderInput {

    /**
     * Cross-chain order structure
     */
    @JsonProperty("order")
    private CrossChainOrderV4 order;

    /**
     * Order signature from wallet
     */
    @JsonProperty("signature")
    private String signature;

    /**
     * Quote ID from quoter service
     */
    @JsonProperty("quoteId")
    private Object quoteId;

    /**
     * Extension data for additional parameters
     */
    @JsonProperty("extension")
    private String extension;

    /**
     * Source chain ID for cross-chain orders
     */
    @JsonProperty("srcChainId")
    private Integer srcChainId;

    /**
     * List of secret hashes for atomic cross-chain execution
     */
    @JsonProperty("secretHashes")
    private List<String> secretHashes;

    /**
     * Order creation timestamp
     */
    @JsonProperty("timestamp")
    private Long timestamp;

    /**
     * Order deadline timestamp
     */
    @JsonProperty("deadline")
    private Long deadline;
}