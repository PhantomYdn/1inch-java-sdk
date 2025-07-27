package io.oneinch.sdk.model.swap;

import io.oneinch.sdk.model.Token;
import io.oneinch.sdk.model.TransactionData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class SwapResponse {
    
    @JsonProperty("srcToken")
    private Token srcToken;
    
    @JsonProperty("dstToken")
    private Token dstToken;
    
    @JsonProperty("dstAmount")
    private BigInteger dstAmount;
    
    @JsonProperty("protocols")
    private List<List<List<SelectedProtocol>>> protocols;
    
    @JsonProperty("tx")
    private TransactionData tx;
}