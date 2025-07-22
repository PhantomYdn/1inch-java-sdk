package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenSearchRequest {
    
    private Integer chainId;
    private String query;
    private Boolean ignoreListed;
    private Boolean onlyPositiveRating;
    private Integer limit;
}