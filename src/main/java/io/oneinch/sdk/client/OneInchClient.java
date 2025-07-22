package io.oneinch.sdk.client;

import io.oneinch.sdk.service.SwapService;
import io.oneinch.sdk.service.SwapServiceImpl;
import io.oneinch.sdk.service.TokenService;
import io.oneinch.sdk.service.TokenServiceImpl;
import lombok.Builder;
import lombok.Getter;
import okhttp3.OkHttpClient;

@Getter
@Builder
public class OneInchClient implements AutoCloseable {

    private static final String[] API_KEY_NAMES = {
        "ONEINCH_API_KEY",
        "oneinch.api.key",
        "1inch.api.key"
    };
    
    private final RetrofitHttpClient httpClient;
    private final SwapService swapService;
    private final TokenService tokenService;

    
    private OneInchClient(String apiKey, OkHttpClient customOkHttpClient) {
        String resolvedApiKey = getApiKeyFromEnvOrExplicit(apiKey);
        this.httpClient = customOkHttpClient != null 
            ? new RetrofitHttpClient(resolvedApiKey, customOkHttpClient)
            : new RetrofitHttpClient(resolvedApiKey);
        this.swapService = new SwapServiceImpl(this.httpClient.getApiService());
        this.tokenService = new TokenServiceImpl(this.httpClient.getTokenApiService(), 
                this.httpClient.getTokenDetailsApiService());
    }
    
    public static OneInchClientBuilder builder() {
        return new OneInchClientBuilder();
    }
    
    public SwapService swap() {
        return swapService;
    }
    
    public TokenService token() {
        return tokenService;
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

        // Check environment variables and system properties for API key
        for (String keyName : API_KEY_NAMES) {
            String envApiKey = System.getenv(keyName);
            if (envApiKey != null && !envApiKey.trim().isEmpty()) {
                return envApiKey;
            }
            String propApiKey = System.getProperty(keyName);
            if (propApiKey != null && !propApiKey.trim().isEmpty()) {
                return propApiKey;
            }
        }
        
        // Neither explicit nor environment variable provided
        throw new IllegalArgumentException("API key is required. Either provide it explicitly or set the " + API_KEY_NAMES[0] + " environment variable.");
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