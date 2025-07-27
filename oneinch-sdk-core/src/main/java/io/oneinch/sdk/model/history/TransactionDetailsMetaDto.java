package io.oneinch.sdk.model.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDetailsMetaDto {

    @JsonProperty("is1inchFusionSwap")
    private Boolean is1inchFusionSwap;

    @JsonProperty("orderFillPercentage")
    private Integer orderFillPercentage;

    @JsonProperty("ensDomainName")
    private String ensDomainName;

    @JsonProperty("fromChainId")
    private String fromChainId;

    @JsonProperty("toChainId")
    private String toChainId;

    @JsonProperty("safeAddress")
    private String safeAddress;

    @JsonProperty("protocol")
    private String protocol;
}