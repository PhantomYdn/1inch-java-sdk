package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenDetailsResponse {
    
    @JsonProperty("assets")
    private AssetsResponse assets;
    
    @JsonProperty("details")
    private DetailsResponse details;
}