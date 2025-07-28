package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Auction point configuration for FusionPlus price curves.
 * Defines a point in the auction timeline with specific pricing parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuctionPoint {

    /**
     * Time delay in seconds from auction start
     */
    @JsonProperty("delay")
    private Integer delay;

    /**
     * Price coefficient at this auction point
     */
    @JsonProperty("coefficient")
    private Double coefficient;
}