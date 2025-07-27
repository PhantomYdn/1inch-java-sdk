package io.oneinch.sdk.model.orderbook;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LimitOrderV4Data {
    private String makerAsset;
    private String takerAsset;
    private String maker;
    private String receiver;
    private String makingAmount;
    private String takingAmount;
    private String salt;
    private String extension;
    private String makerTraits;
}