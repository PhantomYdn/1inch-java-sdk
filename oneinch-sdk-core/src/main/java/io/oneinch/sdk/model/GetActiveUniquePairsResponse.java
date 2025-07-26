package io.oneinch.sdk.model;

import lombok.Data;
import java.util.List;

@Data
public class GetActiveUniquePairsResponse {
    private Meta meta;
    private List<UniquePairs> items;
}