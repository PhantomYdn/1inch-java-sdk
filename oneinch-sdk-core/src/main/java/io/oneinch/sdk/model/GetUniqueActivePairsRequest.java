package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUniqueActivePairsRequest {
    private Integer chainId;
    private Integer page;
    private Integer limit;
}