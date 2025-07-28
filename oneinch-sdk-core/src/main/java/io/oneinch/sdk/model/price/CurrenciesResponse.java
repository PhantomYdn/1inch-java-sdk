package io.oneinch.sdk.model.price;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Response model for supported currencies endpoint
 */
@Data
public class CurrenciesResponse {
    
    /**
     * List of supported currency codes
     */
    @JsonProperty("codes")
    private List<String> codes;
}