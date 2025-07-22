package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DetailsResponse {
    
    @JsonProperty("provider")
    private String provider;
    
    @JsonProperty("providerURL")
    private String providerURL;
    
    @JsonProperty("vol24")
    private Double vol24;
    
    @JsonProperty("marketCap")
    private Double marketCap;
    
    @JsonProperty("circulatingSupply")
    private Double circulatingSupply;
    
    @JsonProperty("totalSupply")
    private Double totalSupply;
}