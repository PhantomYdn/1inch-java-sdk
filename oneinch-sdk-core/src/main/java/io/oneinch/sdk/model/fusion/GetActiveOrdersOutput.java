package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.oneinch.sdk.model.Meta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response containing paginated active orders.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetActiveOrdersOutput {
    
    /**
     * Pagination metadata.
     */
    @JsonProperty("meta")
    private Meta meta;
    
    /**
     * Array of active orders.
     */
    @JsonProperty("items")
    private List<ActiveOrdersOutput> items;
}