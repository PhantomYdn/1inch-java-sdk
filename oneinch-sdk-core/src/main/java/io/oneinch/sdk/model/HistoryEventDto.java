package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryEventDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("address")
    private String address;

    @JsonProperty("type")
    private String type;

    @JsonProperty("rating")
    private String rating;

    @JsonProperty("timeMs")
    private Long timeMs;

    @JsonProperty("details")
    private TransactionDetailsDto details;
}