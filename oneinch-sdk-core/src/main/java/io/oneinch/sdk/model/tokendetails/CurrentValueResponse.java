package io.oneinch.sdk.model.tokendetails;

import io.oneinch.sdk.model.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Response model for current portfolio value breakdown
 */
@Data
public class CurrentValueResponse {
    
    /**
     * Total USD value of all assets
     */
    private Double total;
    
    /**
     * Value breakdown by address
     */
    @JsonProperty("by_address")
    private List<AddressValue> byAddress;
    
    /**
     * Value breakdown by category
     */
    @JsonProperty("by_category") 
    private List<CategoryValue> byCategory;
    
    /**
     * Value breakdown by protocol group
     */
    @JsonProperty("by_protocol_group")
    private List<ProtocolGroupValue> byProtocolGroup;
    
    /**
     * Value breakdown by blockchain
     */
    @JsonProperty("by_chain")
    private List<ChainValue> byChain;
}