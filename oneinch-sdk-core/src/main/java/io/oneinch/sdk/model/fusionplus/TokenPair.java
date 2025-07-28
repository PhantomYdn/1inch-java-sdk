package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token pair configuration for FusionPlus pricing.
 * Represents source and destination token information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenPair {

    /**
     * Source token price or address
     */
    @JsonProperty("srcToken")
    private String srcToken;

    /**
     * Destination token price or address
     */
    @JsonProperty("dstToken")
    private String dstToken;
}