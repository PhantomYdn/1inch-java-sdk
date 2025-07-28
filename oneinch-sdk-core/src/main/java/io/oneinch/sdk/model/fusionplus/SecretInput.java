package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Secret input for FusionPlus cross-chain secret submission.
 * Used in the second phase of cross-chain order execution.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecretInput {

    /**
     * Order hash to submit secret for
     */
    @JsonProperty("orderHash")
    private String orderHash;

    /**
     * The secret value (not the hash)
     */
    @JsonProperty("secret")
    private String secret;

    /**
     * Chain ID where the secret should be submitted
     */
    @JsonProperty("chainId")
    private Integer chainId;

    /**
     * Executor address submitting the secret
     */
    @JsonProperty("executor")
    private String executor;

    /**
     * Timestamp when secret is being submitted
     */
    @JsonProperty("timestamp")
    private Long timestamp;
}