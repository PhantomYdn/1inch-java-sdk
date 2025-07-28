package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Build order request body for FusionPlus.
 * Contains quote information and secret hashes for cross-chain order creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildOrderBody {

    /**
     * Quote object from the /receive endpoint
     */
    @JsonProperty("quote")
    private GetQuoteOutput quote;

    /**
     * Keccak256 hash list of secrets for atomic cross-chain execution
     */
    @JsonProperty("secretsHashList")
    private String secretsHashList;
}