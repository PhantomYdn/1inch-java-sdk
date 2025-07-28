package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order event output for FusionPlus escrow operations.
 * Represents events that occur during cross-chain order lifecycle.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderEventOutput {

    /**
     * Event type identifier
     */
    @JsonProperty("event")
    private String event;

    /**
     * Order hash associated with the event
     */
    @JsonProperty("orderHash")
    private String orderHash;

    /**
     * Transaction hash where event occurred
     */
    @JsonProperty("txHash")
    private String txHash;

    /**
     * Block number where event was emitted
     */
    @JsonProperty("blockNumber")
    private Long blockNumber;

    /**
     * Event timestamp
     */
    @JsonProperty("timestamp")
    private Long timestamp;

    /**
     * Chain ID where event occurred
     */
    @JsonProperty("chainId")
    private Integer chainId;

    /**
     * Event-specific data payload
     */
    @JsonProperty("data")
    private Object data;

    /**
     * Gas used for the transaction
     */
    @JsonProperty("gasUsed")
    private String gasUsed;

    /**
     * Gas price at transaction time
     */
    @JsonProperty("gasPrice")
    private String gasPrice;
}