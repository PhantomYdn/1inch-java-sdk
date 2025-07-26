package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Portfolio API ERC20 tokens overview response
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortfolioTokensResponse {

    @JsonProperty("result")
    private List<Erc20OverviewResponseItem> result;

    @JsonProperty("meta")	
    private PortfolioResponseMeta meta;
}