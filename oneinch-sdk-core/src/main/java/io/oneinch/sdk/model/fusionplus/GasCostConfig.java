package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Gas cost configuration for FusionPlus orders.
 * Defines gas price estimation and bump parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GasCostConfig {

    /**
     * Gas bump estimate for order execution
     */
    @JsonProperty("gasBumpEstimate")
    private Integer gasBumpEstimate;

    /**
     * Gas price estimate in wei (string format for precision)
     */
    @JsonProperty("gasPriceEstimate")
    private String gasPriceEstimate;
}