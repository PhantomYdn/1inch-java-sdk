package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Portfolio API ERC20 token overview response item
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Erc20OverviewResponseItem {

    @JsonProperty("chain_id")
    private Integer chainId;

    @JsonProperty("contract_address")
    private String contractAddress;

    @JsonProperty("name")
    private String name;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("price_to_usd")
    private Double priceToUsd;

    @JsonProperty("value_usd")
    private Double valueUsd;

    @JsonProperty("abs_profit_usd")
    private Double absProfitUsd;

    @JsonProperty("roi")
    private Double roi;

    @JsonProperty("status")
    private Integer status;
}