package io.oneinch.sdk.model;

import lombok.Data;

/**
 * Generic response envelope for Portfolio API v5
 */
@Data
public class ResponseEnvelope<T> {
    private T result;
    private ResponseMeta meta;
}