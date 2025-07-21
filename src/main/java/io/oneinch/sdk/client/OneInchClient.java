package io.oneinch.sdk.client;

import io.oneinch.sdk.service.SwapService;
import io.oneinch.sdk.service.SwapServiceImpl;
import lombok.Builder;
import lombok.Getter;
import okhttp3.OkHttpClient;

@Getter
@Builder
public class OneInchClient implements AutoCloseable {
    
    private final RetrofitHttpClient httpClient;
    private final SwapService swapService;
    
    public OneInchClient(String apiKey) {
        this(apiKey, null);
    }
    
    public OneInchClient(String apiKey, OkHttpClient customOkHttpClient) {
        this.httpClient = customOkHttpClient != null 
            ? new RetrofitHttpClient(apiKey, customOkHttpClient)
            : new RetrofitHttpClient(apiKey);
        this.swapService = new SwapServiceImpl(this.httpClient.getApiService());
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
        private OkHttpClient customOkHttpClient;
        
        public OneInchClientBuilder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }
        
        public OneInchClientBuilder okHttpClient(OkHttpClient okHttpClient) {
            this.customOkHttpClient = okHttpClient;
            return this;
        }
        
        
        public OneInchClient build() {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("API key is required");
            }
            return new OneInchClient(apiKey, customOkHttpClient);
        }
    }
}