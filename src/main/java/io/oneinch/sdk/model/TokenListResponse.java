package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TokenListResponse {
    
    @JsonProperty("keywords")
    private List<String> keywords;
    
    @JsonProperty("logoURI")
    private String logoURI;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("tags")
    private Map<String, TagDto> tags;
    
    @JsonProperty("tags_order")
    private List<String> tagsOrder;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("tokens")
    private List<TokenInfo> tokens;
    
    @JsonProperty("version")
    private VersionDto version;
}