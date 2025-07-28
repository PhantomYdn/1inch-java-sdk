package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Quote presets configuration for FusionPlus.
 * Contains various preset types for different trading strategies.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuotePresets {

    /**
     * Fast preset for quick execution
     */
    @JsonProperty("fast")
    private Preset fast;

    /**
     * Medium preset for balanced execution
     */
    @JsonProperty("medium")
    private Preset medium;

    /**
     * Slow preset for better pricing
     */
    @JsonProperty("slow")
    private Preset slow;

    /**
     * Custom preset with user-defined parameters
     */
    @JsonProperty("custom")
    private Preset custom;
}