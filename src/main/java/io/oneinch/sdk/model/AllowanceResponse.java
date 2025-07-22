package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;

@Data
public class AllowanceResponse {
    
    @JsonProperty("allowance")
    private BigInteger allowance;
}