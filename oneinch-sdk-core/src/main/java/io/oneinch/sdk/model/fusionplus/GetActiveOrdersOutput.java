package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.oneinch.sdk.model.Meta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response model for getting active FusionPlus orders.
 * Enhanced version with cross-chain specific metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetActiveOrdersOutput {

    /**
     * List of active cross-chain orders
     */
    @JsonProperty("items")
    private List<CrossChainOrderDto> items;

    /**
     * Pagination and response metadata
     */
    @JsonProperty("meta")
    private Meta meta;

    /**
     * Total count across all supported chains
     */
    @JsonProperty("totalCrossChainOrders")
    private Integer totalCrossChainOrders;

    /**
     * List of supported source chains
     */
    @JsonProperty("supportedSrcChains")
    private List<Integer> supportedSrcChains;

    /**
     * List of supported destination chains
     */
    @JsonProperty("supportedDstChains")
    private List<Integer> supportedDstChains;
}