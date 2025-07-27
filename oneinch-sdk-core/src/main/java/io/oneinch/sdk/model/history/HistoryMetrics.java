package io.oneinch.sdk.model.history;

import io.oneinch.sdk.model.balance.TokenBalance;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * History metrics for protocols/tokens
 */
@Data
public class HistoryMetrics {
    
    private String index;
    
    @JsonProperty("profit_abs_usd")
    private Double profitAbsUsd;
    
    private Double roi;
    
    @JsonProperty("weighted_apr")
    private Double weightedApr;
    
    @JsonProperty("holding_time_days")
    private Integer holdingTimeDays;
    
    @JsonProperty("rewards_tokens")
    private List<TokenBalance> rewardsTokens;
    
    @JsonProperty("rewards_usd")
    private Double rewardsUsd;
    
    @JsonProperty("claimed_fees")
    private List<TokenBalance> claimedFees;
    
    @JsonProperty("unclaimed_fees")
    private List<TokenBalance> unclaimedFees;
    
    @JsonProperty("impermanent_loss")
    private List<TokenBalance> impermanentLoss;
    
    @JsonProperty("claimed_fees_usd")
    private Double claimedFeesUsd;
    
    @JsonProperty("unclaimed_fees_usd")
    private Double unclaimedFeesUsd;
    
    @JsonProperty("impermanent_loss_usd")
    private Double impermanentLossUsd;
}