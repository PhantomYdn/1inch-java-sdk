package io.oneinch.sdk.model.portfolio;
import io.oneinch.sdk.model.Token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response model for supported chains
 */
@Data
public class SupportedChainResponse {
    
    /**
     * Chain identifier
     */
    @JsonProperty("chain_id")
    private Integer chainId;
    
    /**
     * Human-readable chain name
     */
    @JsonProperty("chain_name")
    private String chainName;
    
    /**
     * Chain icon URL
     */
    @JsonProperty("chain_icon")
    private String chainIcon;
    
    /**
     * Native token
     */
    @JsonProperty("native_token")
    private Token nativeToken;
}
