package io.oneinch.sdk.model.history;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HistoryEventsRequest {

    private String address;
    private Integer limit;
    private String tokenAddress;
    private Integer chainId;
    private String toTimestampMs;
    private String fromTimestampMs;
}