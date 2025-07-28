package io.oneinch.sdk.model.fusion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request for getting Fusion quote with pricing and preset information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FusionQuoteRequest {
    
    /**
     * Chain ID for the network.
     */
    private Integer chainId;
    
    /**
     * Address of "FROM" token.
     * Example: 0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2
     */
    private String fromTokenAddress;
    
    /**
     * Address of "TO" token.
     * Example: 0x6b175474e89094c44da98b954eedeac495271d0f
     */
    private String toTokenAddress;
    
    /**
     * Amount to take from "FROM" token to get "TO" token.
     */
    private String amount;
    
    /**
     * An address of the wallet or contract who will create Fusion order.
     * Example: 0x0000000000000000000000000000000000000000
     */
    private String walletAddress;
    
    /**
     * If enabled then get estimation from 1inch swap builder and generates quoteId.
     * Default: false
     */
    private Boolean enableEstimate;
    
    /**
     * Fee in bps format, 1% is equal to 100bps.
     * Example: 100
     */
    private Integer fee;
    
    /**
     * Show destination amount minus fee.
     */
    private Boolean showDestAmountMinusFee;
    
    /**
     * Permit2 allowance transfer encoded call.
     * Example: "0x"
     */
    private String isPermit2;
    
    /**
     * Enable surplus calculation.
     */
    private Boolean surplus;
    
    /**
     * Permit, user approval sign.
     * Example: "0x"
     */
    private String permit;
    
    /**
     * Slippage configuration.
     */
    private Object slippage;
    
    /**
     * Source configuration.
     */
    private Object source;
}