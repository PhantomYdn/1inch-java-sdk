package io.oneinch.sdk.model;

import java.math.BigInteger;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuoteRequest {
    
    private Integer chainId;
    private String src;
    private String dst;
    private BigInteger amount;
    private String protocols;
    private Double fee;
    private BigInteger gasPrice;
    private Integer complexityLevel;
    private Integer parts;
    private Integer mainRouteParts;
    private BigInteger gasLimit;
    private Boolean includeTokensInfo;
    private Boolean includeProtocols;
    private Boolean includeGas;
    private String connectorTokens;
    private String excludedProtocols;
}