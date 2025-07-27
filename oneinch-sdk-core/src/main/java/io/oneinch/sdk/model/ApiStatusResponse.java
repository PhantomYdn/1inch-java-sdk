package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response model for API status
 */
@Data
public class ApiStatusResponse {
    
    /**
     * API status
     */
    @JsonProperty("is_available")
    private Boolean isAvailable;
}