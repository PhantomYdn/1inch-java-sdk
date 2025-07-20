package io.oneinch.sdk.client;

import io.oneinch.sdk.service.SwapService;
import io.oneinch.sdk.service.SwapServiceImpl;
import lombok.Builder;
import lombok.Getter;
import org.apache.http.impl.client.CloseableHttpClient;

@Getter
@Builder
public class OneInchClient implements AutoCloseable {
    
    private final HttpClient httpClient;
    private final SwapService swapService;
    
    public OneInchClient(String apiKey) {
        this(apiKey, null);
    }
    
    public OneInchClient(String apiKey, CloseableHttpClient customHttpClient) {
        this.httpClient = customHttpClient != null 
            ? new ApacheHttpClient(apiKey, customHttpClient)
            : new ApacheHttpClient(apiKey);
        this.swapService = new SwapServiceImpl(this.httpClient);
    }
    
    public static OneInchClientBuilder builder() {
        return new OneInchClientBuilder();
    }
    
    public SwapService swap() {
        return swapService;
    }
    
    @Override
    public void close() throws Exception {
        httpClient.close();
    }
    
    public static class OneInchClientBuilder {
        private String apiKey;
        private CloseableHttpClient customHttpClient;
        
        public OneInchClientBuilder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }
        
        public OneInchClientBuilder httpClient(CloseableHttpClient httpClient) {
            this.customHttpClient = httpClient;
            return this;
        }
        
        public OneInchClient build() {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("API key is required");
            }
            return new OneInchClient(apiKey, customHttpClient);
        }
    }
}