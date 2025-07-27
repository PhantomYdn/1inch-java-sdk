package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response metadata for Portfolio API v5
 */
@Data
public class ResponseMeta {
    
    @JsonProperty("cached_at")
    private Long cachedAt;
    
    private ProcessingInfo system;
}