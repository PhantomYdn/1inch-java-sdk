package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gas cost configuration for Fusion orders.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GasCostConfigClass {
    
    /**
     * Gas bump estimate for the order.
     */
    @JsonProperty("gasBumpEstimate")
    private Integer gasBumpEstimate;
    
    /**
     * Gas price estimate in string format.
     */
    @JsonProperty("gasPriceEstimate")
    private String gasPriceEstimate;
}