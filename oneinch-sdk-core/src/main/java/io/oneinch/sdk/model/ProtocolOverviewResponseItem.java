package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Portfolio API protocol overview response item
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProtocolOverviewResponseItem {

    @JsonProperty("chain_id")
    private Integer chainId;

    @JsonProperty("contract_address")
    private String contractAddress;

    @JsonProperty("token_id")
    private BigInteger tokenId;

    @JsonProperty("addresses")
    private List<String> addresses;

    @JsonProperty("protocol")
    private String protocol;

    @JsonProperty("name")
    private String name;

    @JsonProperty("contract_type")
    private String contractType;

    @JsonProperty("sub_contract_type")
    private String subContractType;

    @JsonProperty("is_whitelisted")
    private Integer isWhitelisted;

    @JsonProperty("protocol_name")
    private String protocolName;

    @JsonProperty("protocol_icon")
    private String protocolIcon;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("token_address")
    private String tokenAddress;

    @JsonProperty("underlying_tokens")
    private List<ProtocolUnderlyingTokenResponseItem> underlyingTokens;

    @JsonProperty("value_usd")
    private Double valueUsd;

    @JsonProperty("debt")
    private Boolean debt;

    @JsonProperty("rewards_tokens")
    private List<ProtocolUnderlyingTokenResponseItem> rewardsTokens;

    @JsonProperty("profit_abs_usd")
    private Double profitAbsUsd;

    @JsonProperty("roi")
    private Double roi;

    @JsonProperty("weighted_apr")
    private Double weightedApr;

    @JsonProperty("holding_time_days")
    private Integer holdingTimeDays;

    @JsonProperty("info")
    private Map<String, Object> info;
}