package io.oneinch.sdk.model.tokendetails;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChartIntervalRequest {
    
    private Integer chainId;
    private String tokenAddress;
    private String interval;
    private String provider;
    private Long fromTime;
}