package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Value breakdown by category (tokens, native, protocols)
 */
@Data
public class CategoryValue {
    
    /**
     * USD value of assets in this category
     */
    @JsonProperty("value_usd")
    private Double valueUsd;
    
    /**
     * Category identifier (tokens, native, protocols)
     */
    @JsonProperty("category_id")
    private String categoryId;
    
    /**
     * Human-readable category name
     */
    @JsonProperty("category_name")
    private String categoryName;
}