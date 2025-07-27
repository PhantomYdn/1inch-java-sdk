package io.oneinch.sdk.model.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenActionDto {

    @JsonProperty("address")
    private String address;

    @JsonProperty("standard")
    private String standard;

    @JsonProperty("fromAddress")
    private String fromAddress;

    @JsonProperty("toAddress")
    private String toAddress;

    @JsonProperty("tokenId")
    private Object tokenId;

    @JsonProperty("amount")
    private BigInteger amount;

    @JsonProperty("direction")
    private String direction;
}