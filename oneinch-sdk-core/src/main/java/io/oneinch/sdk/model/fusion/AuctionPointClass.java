package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a point in the auction pricing mechanism.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionPointClass {
    
    /**
     * Delay in seconds from the previous point or auction start time.
     */
    @JsonProperty("delay")
    private Integer delay;
    
    /**
     * Coefficient representing the price change at this point.
     */
    @JsonProperty("coefficient")
    private Integer coefficient;
}