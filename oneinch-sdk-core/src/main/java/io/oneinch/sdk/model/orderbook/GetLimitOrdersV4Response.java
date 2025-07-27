package io.oneinch.sdk.model.orderbook;

import lombok.Data;
import java.util.List;

@Data
public class GetLimitOrdersV4Response {
    private String signature;
    private String orderHash;
    private String createDateTime;
    private String remainingMakerAmount;
    private String makerBalance;
    private String makerAllowance;
    private LimitOrderV4Data data;
    private String makerRate;
    private String takerRate;
    private boolean isMakerContract;
    private List<String> orderInvalidReason;
}