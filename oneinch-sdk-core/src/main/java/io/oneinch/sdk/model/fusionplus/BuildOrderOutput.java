package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Build order output for FusionPlus cross-chain orders.
 * Contains EIP712 typed data and order metadata for signing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildOrderOutput {

    /**
     * EIP712 typed data structure for order signing
     */
    @JsonProperty("typedData")
    private Object typedData;

    /**
     * Hash of the cross-chain order
     */
    @JsonProperty("orderHash")
    private String orderHash;

    /**
     * Cross-chain order extension data
     */
    @JsonProperty("extension")
    private String extension;
}