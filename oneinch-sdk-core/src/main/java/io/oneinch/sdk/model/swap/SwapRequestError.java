package io.oneinch.sdk.model.swap;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.oneinch.sdk.exception.OneInchApiException;
import lombok.Data;

import java.util.List;

@Data
public class SwapRequestError {
    
    @JsonProperty("error")
    private String error;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("statusCode")
    private Integer statusCode;
    
    @JsonProperty("requestId")
    private String requestId;
    
    @JsonProperty("meta")
    private List<OneInchApiException.HttpExceptionMeta> meta;
}