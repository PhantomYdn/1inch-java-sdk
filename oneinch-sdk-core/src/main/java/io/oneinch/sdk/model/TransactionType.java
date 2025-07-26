package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {
    APPROVE("Approve"),
    WRAP("Wrap"),
    UNWRAP("Unwrap"),
    TRANSFER("Transfer"),
    SWAP_EXACT_INPUT("SwapExactInput"),
    SWAP_EXACT_OUTPUT("SwapExactOutput"),
    LIMIT_ORDER_FILL("LimitOrderFill"),
    LIMIT_ORDER_CANCEL("LimitOrderCancel"),
    LIMIT_ORDER_CANCEL_ALL("LimitOrderCancelAll"),
    MULTICALL("Multicall"),
    ADD_LIQUIDITY("AddLiquidity"),
    REMOVE_LIQUIDITY("RemoveLiquidity"),
    BORROW("Borrow"),
    REPAY("Repay"),
    STAKE("Stake"),
    UNSTAKE("Unstake"),
    VOTE("Vote"),
    DELEGATE_VOTE_POWER("DelegateVotePower"),
    UN_DELEGATE_VOTE_POWER("UnDelegateVotePower"),
    DISCARD_VOTE("DiscardVote"),
    DEPLOY_POOL("DeployPool"),
    CLAIM("Claim"),
    ABI_DECODED("AbiDecoded"),
    TRACE_DECODED("TraceDecoded"),
    ACTION("Action"),
    BRIDGE("Bridge"),
    BUY_NFT("BuyNft"),
    BID_NFT("BidNft"),
    OFFER_SELL_NFT("OfferSellNft"),
    BURN("Burn"),
    WRAPPED_TX("WrappedTx"),
    REGISTER_ENS_DOMAIN("RegisterENSDomain"),
    REVOKE("Revoke"),
    CREATE_SAFE("CreateSafe"),
    ADD_OWNER("AddOwner");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TransactionType fromValue(String value) {
        for (TransactionType type : TransactionType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown TransactionType: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}