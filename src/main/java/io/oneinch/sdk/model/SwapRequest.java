package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SwapRequest {
    
    private String src;
    private String dst;
    private String amount;
    private String from;
    private String origin;
    private Double slippage;
    private String protocols;
    private Double fee;
    private String gasPrice;
    private Integer complexityLevel;
    private Integer parts;
    private Integer mainRouteParts;
    private Long gasLimit;
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