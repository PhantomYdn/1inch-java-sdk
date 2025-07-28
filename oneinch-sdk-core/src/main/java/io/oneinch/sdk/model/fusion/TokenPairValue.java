package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Container for token pair values in different currencies.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPairValue {
    
    /**
     * USD pricing information for the token pair.
     */
    @JsonProperty("usd")
    private PairCurrencyValue usd;
}