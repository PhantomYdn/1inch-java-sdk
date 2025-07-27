package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Adapter result for protocols/tokens snapshot
 */
@Data
public class AdapterResult {
    
    @JsonProperty("chain_id")
    private Integer chainId;
    
    @JsonProperty("contract_address")
    private String contractAddress;
    
    @JsonProperty("token_id")
    private Integer tokenId;
    
    private String address;
    
    @JsonProperty("block_number_created")
    private Long blockNumberCreated;
    
    @JsonProperty("block_number")
    private Long blockNumber;
    
    private Long timestamp;
    
    @JsonProperty("protocol_type")
    private String protocolType;
    
    @JsonProperty("protocol_handler_id")
    private String protocolHandlerId;
    
    @JsonProperty("protocol_group_id")
    private String protocolGroupId;
    
    @JsonProperty("protocol_group_name")
    private String protocolGroupName;
    
    @JsonProperty("protocol_group_icon")
    private String protocolGroupIcon;
    
    @JsonProperty("protocol_sub_group_id")
    private String protocolSubGroupId;
    
    @JsonProperty("protocol_sub_group_name")
    private String protocolSubGroupName;
    
    @JsonProperty("contract_name")
    private String contractName;
    
    @JsonProperty("contract_symbol")
    private String contractSymbol;
    
    @JsonProperty("asset_sign")
    private Integer assetSign;
    
    private Integer status;
    
    @JsonProperty("underlying_tokens")
    private List<TokenBalance> underlyingTokens;
    
    @JsonProperty("reward_tokens")
    private List<TokenBalance> rewardTokens;
    
    @JsonProperty("value_usd")
    private Double valueUsd;
    
    private Boolean locked;
    
    private String index;
}