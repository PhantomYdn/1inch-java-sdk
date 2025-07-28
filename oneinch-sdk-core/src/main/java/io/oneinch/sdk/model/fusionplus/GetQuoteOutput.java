package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FusionPlus quote output with cross-chain enhancements.
 * Contains pricing information and preset configurations for cross-chain swaps.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetQuoteOutput {

    /**
     * Generated quote identifier for order creation
     */
    @JsonProperty("quoteId")
    private Object quoteId;

    /**
     * Source token amount (in wei string format)
     */
    @JsonProperty("srcTokenAmount")
    private String srcTokenAmount;

    /**
     * Destination token amount (in wei string format)
     */
    @JsonProperty("dstTokenAmount")
    private String dstTokenAmount;

    /**
     * Available preset configurations
     */
    @JsonProperty("presets")
    private QuotePresets presets;

    /**
     * Source chain escrow factory contract address
     */
    @JsonProperty("srcEscrowFactory")
    private String srcEscrowFactory;

    /**
     * Destination chain escrow factory contract address
     */
    @JsonProperty("dstEscrowFactory")
    private String dstEscrowFactory;

    /**
     * List of authorized executor addresses
     */
    @JsonProperty("whitelist")
    private List<String> whitelist;

    /**
     * Time lock configuration for cross-chain operations
     */
    @JsonProperty("timeLocks")
    private TimeLocks timeLocks;

    /**
     * Safety deposit amount for source chain (in wei)
     */
    @JsonProperty("srcSafetyDeposit")
    private String srcSafetyDeposit;

    /**
     * Safety deposit amount for destination chain (in wei)
     */
    @JsonProperty("dstSafetyDeposit")
    private String dstSafetyDeposit;

    /**
     * Recommended preset type
     */
    @JsonProperty("recommendedPreset")
    private String recommendedPreset;

    /**
     * Price information in different currencies
     */
    @JsonProperty("prices")
    private PairCurrency prices;

    /**
     * Volume information in different currencies
     */
    @JsonProperty("volume")
    private PairCurrency volume;
}