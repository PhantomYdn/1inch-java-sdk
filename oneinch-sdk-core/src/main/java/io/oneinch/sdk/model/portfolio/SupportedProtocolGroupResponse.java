package io.oneinch.sdk.model.portfolio;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response model for supported protocol groups
 */
@Data
public class SupportedProtocolGroupResponse {
    
    /**
     * Chain identifier
     */
    @JsonProperty("chain_id")
    private Integer chainId;
    
    /**
     * Protocol group identifier
     */
    @JsonProperty("protocol_group_id")
    private String protocolGroupId;
    
    /**
     * Human-readable protocol group name
     */
    @JsonProperty("protocol_group_name")
    private String protocolGroupName;
    
    /**
     * Protocol group icon URL
     */
    @JsonProperty("protocol_group_icon")
    private String protocolGroupIcon;
}