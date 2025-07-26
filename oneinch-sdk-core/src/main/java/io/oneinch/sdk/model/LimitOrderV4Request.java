package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LimitOrderV4Request {
    private Integer chainId;
    private String orderHash;
    private String signature;
    private LimitOrderV4Data data;
}