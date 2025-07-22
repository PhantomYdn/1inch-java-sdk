package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class ApproveTransactionRequest {
    
    private String tokenAddress;
    private BigInteger amount;
}