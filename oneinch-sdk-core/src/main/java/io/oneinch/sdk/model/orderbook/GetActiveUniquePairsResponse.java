package io.oneinch.sdk.model.orderbook;

import io.oneinch.sdk.model.Meta;
import lombok.Data;
import java.util.List;

@Data
public class GetActiveUniquePairsResponse {
    private Meta meta;
    private List<UniquePairs> items;
}