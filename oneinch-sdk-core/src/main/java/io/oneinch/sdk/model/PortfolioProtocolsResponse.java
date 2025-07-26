package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Portfolio API protocols overview response
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortfolioProtocolsResponse {

    @JsonProperty("result")
    private List<ProtocolOverviewResponseItem> result;

    @JsonProperty("meta")
    private PortfolioResponseMeta meta;
}