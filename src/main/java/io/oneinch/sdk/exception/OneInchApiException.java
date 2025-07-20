package io.oneinch.sdk.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class OneInchApiException extends OneInchException {
    
    private final String error;
    private final String description;
    private final int statusCode;
    private final String requestId;
    private final List<HttpExceptionMeta> meta;
    
    public OneInchApiException(String error, String description, int statusCode, String requestId, List<HttpExceptionMeta> meta) {
        super(String.format("API Error [%d]: %s - %s (Request ID: %s)", statusCode, error, description, requestId));
        this.error = error;
        this.description = description;
        this.statusCode = statusCode;
        this.requestId = requestId;
        this.meta = meta;
    }
    
    @Getter
    public static class HttpExceptionMeta {
        private final String type;
        private final String value;
        
        public HttpExceptionMeta(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }
}