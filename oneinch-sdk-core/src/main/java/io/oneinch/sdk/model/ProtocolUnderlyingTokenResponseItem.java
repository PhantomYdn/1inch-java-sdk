package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Protocol underlying token response item
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProtocolUnderlyingTokenResponseItem {

    @JsonProperty("chain_id")
    private Integer chainId;

    @JsonProperty("address")
    private String address;

    @JsonProperty("name")
    private String name;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("decimals")
    private Integer decimals;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("price_to_usd")
    private Double priceToUsd;

    @JsonProperty("value_usd")
    private Double valueUsd;
}