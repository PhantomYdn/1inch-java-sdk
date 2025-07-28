package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request for getting active FusionPlus orders.
 * Enhanced with cross-chain filtering capabilities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FusionPlusActiveOrdersRequest {

    /**
     * Source chain ID to filter orders
     */
    @JsonProperty("srcChain")
    private Integer srcChain;

    /**
     * Destination chain ID to filter orders
     */
    @JsonProperty("dstChain")
    private Integer dstChain;

    /**
     * Page number for pagination
     */
    @JsonProperty("page")
    private Integer page;

    /**
     * Number of items per page
     */
    @JsonProperty("limit")
    private Integer limit;

    /**
     * Sort by field
     */
    @JsonProperty("sortBy")
    private String sortBy;
}