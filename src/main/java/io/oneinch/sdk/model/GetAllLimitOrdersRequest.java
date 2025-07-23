package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAllLimitOrdersRequest {
    private Integer chainId;
    private Integer page;
    private Integer limit;
    private String statuses;
    private String sortBy;
    private String takerAsset;
    private String makerAsset;
}