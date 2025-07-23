package io.oneinch.sdk.model;

import lombok.Data;

@Data
public class GetEventsV4Response {
    private Long id;
    private Integer network;
    private String logId;
    private Integer version;
    private String action;
    private String orderHash;
    private String taker;
    private String remainingMakerAmount;
    private String transactionHash;
    private Long blockNumber;
    private String createDateTime;
}