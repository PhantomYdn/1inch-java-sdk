package io.oneinch.sdk.model.swap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SpenderResponse {
    
    @JsonProperty("address")
    private String address;
}