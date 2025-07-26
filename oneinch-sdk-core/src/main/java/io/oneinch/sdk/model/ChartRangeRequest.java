package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChartRangeRequest {
    
    private Integer chainId;
    private String tokenAddress;
    private Long from;
    private Long to;
    private String provider;
    private Long fromTime;
}