package io.oneinch.sdk.model.orderbook;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetEventsRequest {
    private Integer chainId;
    private Integer limit;
}