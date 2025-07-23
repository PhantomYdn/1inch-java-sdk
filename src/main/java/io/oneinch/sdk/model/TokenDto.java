package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class TokenDto {
    
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
    
    @JsonProperty("rating")
    private Double rating;
    
    @JsonProperty("eip2612")
    private Boolean eip2612;
    
    @JsonProperty("isFoT")
    private Boolean isFoT;
    
    @JsonProperty("tags")
    private List<TagDto> tags;
    
    @JsonProperty("providers")
    private List<String> providers;

    @JsonProperty("marketCap")
    private BigInteger marketCap;
}