package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDetailsDto {

    @JsonProperty("orderInBlock")
    private Integer orderInBlock;

    @JsonProperty("txHash")
    private String txHash;

    @JsonProperty("chainId")
    private Integer chainId;

    @JsonProperty("blockNumber")
    private Long blockNumber;

    @JsonProperty("blockTimeSec")
    private Long blockTimeSec;

    @JsonProperty("status")
    private TransactionStatus status;

    @JsonProperty("type")
    private TransactionType type;

    @JsonProperty("tokenActions")
    private List<TokenActionDto> tokenActions;

    @JsonProperty("fromAddress")
    private String fromAddress;

    @JsonProperty("toAddress")
    private String toAddress;

    @JsonProperty("nonce")
    private Integer nonce;

    @JsonProperty("feeInWei")
    private BigInteger feeInWei;

    @JsonProperty("meta")
    private TransactionDetailsMetaDto meta;
}