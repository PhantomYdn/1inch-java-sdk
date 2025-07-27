package io.oneinch.sdk.model.swap;

import java.math.BigInteger;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SwapRequest {
    
    private Integer chainId;
    private String src;
    private String dst;
    private BigInteger amount;
    private String from;
    private String origin;
    private Double slippage;
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
    private String permit;
    private String receiver;
    private String referrer;
    private Boolean allowPartialFill;
    private Boolean disableEstimate;
    private Boolean usePermit2;
}