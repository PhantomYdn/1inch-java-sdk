package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a point in the Fusion auction pricing curve.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionPointOutput {
    
    /**
     * The delay in seconds from the previous point or auction start time.
     */
    @JsonProperty("delay")
    private Integer delay;
    
    /**
     * The rate bump from the order min taker amount.
     */
    @JsonProperty("coefficient")
    private Integer coefficient;
}