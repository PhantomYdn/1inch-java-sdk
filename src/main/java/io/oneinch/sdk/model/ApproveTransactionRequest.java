package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApproveTransactionRequest {
    
    private String tokenAddress;
    private String amount;
}