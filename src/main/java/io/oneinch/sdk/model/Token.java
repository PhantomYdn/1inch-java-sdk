package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {
    
    @JsonProperty("chainId")
    private Integer chainId;
    
    @JsonProperty("address")
    private String address;
    
    @JsonProperty("symbol")
    private String symbol;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("decimals")
    private Integer decimals;
    
    @JsonProperty("logoURI")
    private String logoURI;
    
    @JsonProperty("domainVersion")
    private String domainVersion;
    
    @JsonProperty("eip2612")
    private Boolean eip2612;
    
    @JsonProperty("isFoT")
    private Boolean isFoT;
    
    @JsonProperty("rating")
    private Double rating;
    
    @JsonProperty("providers")
    private List<String> providers;
    
    @JsonProperty("marketCap")
    private BigInteger marketCap;
    
    @JsonProperty("tags")
    @JsonDeserialize(using = FlexibleTagsDeserializer.class)
    private List<Object> tags;
}