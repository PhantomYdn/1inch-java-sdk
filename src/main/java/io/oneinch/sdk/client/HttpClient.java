package io.oneinch.sdk.client;

import io.oneinch.sdk.exception.OneInchException;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface HttpClient {
    
    <T> T get(String path, Map<String, Object> params, Class<T> responseType) throws OneInchException;
    
    <T> CompletableFuture<T> getAsync(String path, Map<String, Object> params, Class<T> responseType);
    
    void close() throws Exception;
}