package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TagDto {
    
    @JsonProperty("provider")
    private String provider;
    
    @JsonProperty("value")
    private String value;
}