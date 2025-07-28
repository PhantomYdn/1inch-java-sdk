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
 * Response model for FusionPlus active orders.
 * Contains paginated list of active cross-chain orders.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActiveOrdersOutput {

    /**
     * List of active cross-chain orders
     */
    @JsonProperty("items")
    private List<CrossChainOrderDto> items;

    /**
     * Pagination metadata
     */
    @JsonProperty("meta")
    private Meta meta;
}