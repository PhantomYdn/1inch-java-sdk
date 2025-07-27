package io.oneinch.sdk.model.swap;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AllowanceRequest {
    private Integer chainId;
    private String tokenAddress;
    private String walletAddress;
}