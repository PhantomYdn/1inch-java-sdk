package io.oneinch.sdk.model.tokendetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ChartDataResponse {
    
    @JsonProperty("d")
    private List<ChartPointResponse> data;
    
    @JsonProperty("p")
    private String provider;
}