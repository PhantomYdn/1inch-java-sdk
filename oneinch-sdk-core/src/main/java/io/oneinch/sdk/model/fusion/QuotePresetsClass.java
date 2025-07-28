package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Container for different preset types available for Fusion orders.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotePresetsClass {
    
    /**
     * Fast preset type - quickest execution with higher fees.
     */
    @JsonProperty("fast")
    private PresetClass fast;
    
    /**
     * Medium preset type - balanced execution time and fees.
     */
    @JsonProperty("medium")
    private PresetClass medium;
    
    /**
     * Slow preset type - longer execution time with lower fees.
     */
    @JsonProperty("slow")
    private PresetClass slow;
    
    /**
     * Custom preset type - user-defined configuration.
     */
    @JsonProperty("custom")
    private PresetClass custom;
}