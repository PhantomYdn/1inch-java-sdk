package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomTokenRequest {
    
    private Integer chainId;
    private List<String> addresses;
}