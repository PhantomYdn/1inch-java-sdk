package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VersionDto {
    
    @JsonProperty("major")
    private Integer major;
    
    @JsonProperty("minor")
    private Integer minor;
    
    @JsonProperty("patch")
    private Integer patch;
}