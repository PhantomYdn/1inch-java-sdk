package io.oneinch.sdk.model.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoryResponseDto {

    @JsonProperty("items")
    private List<HistoryEventDto> items;

    @JsonProperty("cache_counter")
    private Integer cacheCounter;
}