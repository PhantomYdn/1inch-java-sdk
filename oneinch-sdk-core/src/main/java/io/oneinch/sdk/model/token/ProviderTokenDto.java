package io.oneinch.sdk.model.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProviderTokenDto {
    
    @JsonProperty("chainId")
    private Integer chainId;
    
    @JsonProperty("symbol")
    private String symbol;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("address")
    private String address;
    
    @JsonProperty("decimals")
    private Integer decimals;
    
    @JsonProperty("logoURI")
    private String logoURI;
    
    @JsonProperty("providers")
    private List<String> providers;
    
    @JsonProperty("eip2612")
    private Boolean eip2612;
    
    @JsonProperty("isFoT")
    private Boolean isFoT;
    
    @JsonProperty("displayedSymbol")
    private String displayedSymbol;
    
    @JsonProperty("tags")
    private List<String> tags;
}