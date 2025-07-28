package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents currency pair values for pricing information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PairCurrencyValue {
    
    /**
     * From token value in the currency.
     */
    @JsonProperty("fromToken")
    private String fromToken;
    
    /**
     * To token value in the currency.
     */
    @JsonProperty("toToken")
    private String toToken;
}