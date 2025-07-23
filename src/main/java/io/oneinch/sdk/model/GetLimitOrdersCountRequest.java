package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetLimitOrdersCountRequest {
    private Integer chainId;
    private String statuses;
    private String takerAsset;
    private String makerAsset;
}