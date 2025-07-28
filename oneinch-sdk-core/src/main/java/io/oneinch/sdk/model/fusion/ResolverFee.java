package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents resolver fee configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolverFee {
    
    /**
     * Resolver fee receiver address.
     */
    @JsonProperty("receiver")
    private String receiver;
    
    /**
     * Resolver fee in basis points.
     */
    @JsonProperty("bps")
    private Integer bps;
    
    /**
     * Whitelist discount percentage.
     */
    @JsonProperty("whitelistDiscountPercent")
    private Integer whitelistDiscountPercent;
}