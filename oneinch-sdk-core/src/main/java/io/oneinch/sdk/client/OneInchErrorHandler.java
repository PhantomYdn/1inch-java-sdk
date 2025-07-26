package io.oneinch.sdk.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oneinch.sdk.exception.OneInchApiException;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.QuoteRequestError;
import io.oneinch.sdk.model.SwapRequestError;
import lombok.extern.slf4j.Slf4j;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.IOException;

@Slf4j
public class OneInchErrorHandler {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static OneInchException handleError(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return handleHttpException((HttpException) throwable);
        } else if (throwable instanceof IOException) {
            return new OneInchException("Network error: " + throwable.getMessage(), throwable);
        } else {
            return new OneInchException("Unexpected error: " + throwable.getMessage(), throwable);
        }
    }
    
    private static OneInchException handleHttpException(HttpException httpException) {
        try {
            Response<?> response = httpException.response();
            if (response != null && response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                log.debug("Error response body: {}", errorBody);
                
                return parseApiError(errorBody, response.code());
            }
        } catch (Exception e) {
            log.warn("Failed to parse error response", e);
        }
        
        return new OneInchException("HTTP " + httpException.code() + ": " + httpException.message());
    }
    
    private static OneInchException parseApiError(String errorBody, int statusCode) {
        try {
            // Try parsing as QuoteRequestError first
            QuoteRequestError quoteError = objectMapper.readValue(errorBody, QuoteRequestError.class);
            return new OneInchApiException(
                    quoteError.getError(), 
                    quoteError.getDescription(),
                    quoteError.getStatusCode(), 
                    quoteError.getRequestId(), 
                    quoteError.getMeta()
            );
        } catch (Exception e1) {
            try {
                // Try parsing as SwapRequestError
                SwapRequestError swapError = objectMapper.readValue(errorBody, SwapRequestError.class);
                return new OneInchApiException(
                        swapError.getError(), 
                        swapError.getDescription(),
                        swapError.getStatusCode(), 
                        swapError.getRequestId(), 
                        swapError.getMeta()
                );
            } catch (Exception e2) {
                log.warn("Failed to parse API error response", e2);
                return new OneInchException("HTTP " + statusCode + ": " + errorBody);
            }
        }
    }
}