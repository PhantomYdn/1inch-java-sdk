package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Cross-chain order data transfer object for FusionPlus.
 * Represents a cross-chain order with enhanced features including escrow management.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrossChainOrderDto {

    /**
     * The order hash identifier
     */
    @JsonProperty("orderHash")
    private String orderHash;

    /**
     * Order signature
     */
    @JsonProperty("signature")
    private String signature;

    /**
     * Order execution deadline timestamp
     */
    @JsonProperty("deadline")
    private Long deadline;

    /**
     * Auction start timestamp
     */
    @JsonProperty("auctionStartDate")
    private Long auctionStartDate;

    /**
     * Auction end timestamp
     */
    @JsonProperty("auctionEndDate")
    private Long auctionEndDate;

    /**
     * Remaining maker amount available
     */
    @JsonProperty("remainingMakerAmount")
    private String remainingMakerAmount;

    /**
     * Order data structure
     */
    @JsonProperty("order")
    private CrossChainOrderV4 order;

    /**
     * Extension data for additional parameters
     */
    @JsonProperty("extension")
    private String extension;

    /**
     * Order interaction calldata
     */
    @JsonProperty("interaction")
    private String interaction;

    /**
     * Source chain ID where the order originates
     */
    @JsonProperty("srcChainId")
    private Integer srcChainId;

    /**
     * Destination chain ID where the order will be fulfilled
     */
    @JsonProperty("dstChainId")
    private Integer dstChainId;

    /**
     * List of secret hashes for atomic cross-chain execution
     */
    @JsonProperty("secretHashes")
    private List<String> secretHashes;

    /**
     * Status of the cross-chain order
     */
    @JsonProperty("status")
    private String status;

    /**
     * Creation timestamp
     */
    @JsonProperty("createdAt")
    private Long createdAt;

    /**
     * Source escrow factory contract address
     */
    @JsonProperty("srcEscrowFactory")
    private String srcEscrowFactory;

    /**
     * Destination escrow factory contract address
     */
    @JsonProperty("dstEscrowFactory")
    private String dstEscrowFactory;

    /**
     * Safety deposit amount for source chain
     */
    @JsonProperty("srcSafetyDeposit")
    private String srcSafetyDeposit;

    /**
     * Safety deposit amount for destination chain
     */
    @JsonProperty("dstSafetyDeposit")
    private String dstSafetyDeposit;
}