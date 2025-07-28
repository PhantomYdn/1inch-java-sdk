package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Input for creating custom preset configurations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomPresetInput {
    
    /**
     * Duration of the auction in seconds.
     */
    @JsonProperty("auctionDuration")
    private Integer auctionDuration;
    
    /**
     * Auction start amount.
     */
    @JsonProperty("auctionStartAmount")
    private Long auctionStartAmount;
    
    /**
     * Auction end amount.
     */
    @JsonProperty("auctionEndAmount")
    private Long auctionEndAmount;
    
    /**
     * Custom auction points configuration.
     */
    @JsonProperty("points")
    private List<String> points;
}