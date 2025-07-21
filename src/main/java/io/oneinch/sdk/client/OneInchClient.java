package io.oneinch.sdk.client;

import io.oneinch.sdk.service.SwapService;
import io.oneinch.sdk.service.SwapServiceImpl;
import lombok.Builder;
import lombok.Getter;
import okhttp3.OkHttpClient;

@Getter
@Builder
public class OneInchClient implements AutoCloseable {
    
    private static final String API_KEY_ENV_VAR = "ONEINCH_API_KEY";
    
    private final RetrofitHttpClient httpClient;
    private final SwapService swapService;
    
    public OneInchClient() {
        this(null, null);
    }
    
    public OneInchClient(String apiKey) {
        this(apiKey, null);
    }
    
    public OneInchClient(String apiKey, OkHttpClient customOkHttpClient) {
        String resolvedApiKey = getApiKeyFromEnvOrExplicit(apiKey);
        this.httpClient = customOkHttpClient != null 
            ? new RetrofitHttpClient(resolvedApiKey, customOkHttpClient)
            : new RetrofitHttpClient(resolvedApiKey);
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
    
    private static String getApiKeyFromEnvOrExplicit(String explicitApiKey) {
        // If explicit API key is provided and not empty, use it
        if (explicitApiKey != null && !explicitApiKey.trim().isEmpty()) {
            return explicitApiKey;
        }
        
        // Try to get from environment variable
        String envApiKey = System.getenv(API_KEY_ENV_VAR);
        if (envApiKey != null && !envApiKey.trim().isEmpty()) {
            return envApiKey;
        }
        
        // Neither explicit nor environment variable provided
        throw new IllegalArgumentException("API key is required. Either provide it explicitly or set the " + API_KEY_ENV_VAR + " environment variable.");
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
            return new OneInchClient(apiKey, customOkHttpClient);
        }
    }
}