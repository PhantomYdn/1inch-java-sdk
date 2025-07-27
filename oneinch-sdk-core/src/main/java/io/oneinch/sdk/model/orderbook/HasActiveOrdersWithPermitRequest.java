package io.oneinch.sdk.model.orderbook;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HasActiveOrdersWithPermitRequest {
    private Integer chainId;
    private String walletAddress;
    private String token;
}