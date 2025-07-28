package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Public action output for FusionPlus cross-chain operations.
 * Represents actions that can be performed by anyone after time locks expire.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicActionOutput {

    /**
     * Order hash for the public action
     */
    @JsonProperty("orderHash")
    private String orderHash;

    /**
     * Action type (e.g., "withdrawal", "cancellation")
     */
    @JsonProperty("actionType")
    private String actionType;

    /**
     * Chain ID where action can be performed
     */
    @JsonProperty("chainId")
    private Integer chainId;

    /**
     * Calldata for executing the public action
     */
    @JsonProperty("calldata")
    private String calldata;

    /**
     * Target contract address for the action
     */
    @JsonProperty("target")
    private String target;

    /**
     * Value to send with the transaction (in wei)
     */
    @JsonProperty("value")
    private String value;

    /**
     * Timestamp when this action becomes available
     */
    @JsonProperty("availableAt")
    private Long availableAt;

    /**
     * Whether this action is currently available
     */
    @JsonProperty("available")
    private Boolean available;

    /**
     * Gas estimate for the action
     */
    @JsonProperty("gasEstimate")
    private String gasEstimate;
}