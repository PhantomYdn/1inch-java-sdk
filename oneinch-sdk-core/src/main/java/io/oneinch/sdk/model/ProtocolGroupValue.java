package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Value breakdown by protocol group
 */
@Data
public class ProtocolGroupValue {
    
    /**
     * USD value of assets in this protocol group
     */
    @JsonProperty("value_usd")
    private Double valueUsd;
    
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
}