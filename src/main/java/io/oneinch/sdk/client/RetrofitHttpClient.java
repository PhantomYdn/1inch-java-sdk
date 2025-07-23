package io.oneinch.sdk.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oneinch.sdk.exception.OneInchException;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RetrofitHttpClient implements HttpClient {
    
    private static final String BASE_URL = "https://api.1inch.dev/";
    
    private final OneInchSwapApiService apiService;
    private final OneInchTokenApiService tokenApiService;
    private final OneInchTokenDetailsApiService tokenDetailsApiService;
    private final OneInchOrderbookApiService orderbookApiService;
    private final OkHttpClient okHttpClient;
    
    public RetrofitHttpClient(String apiKey) {
        this(apiKey, createDefaultOkHttpClient(apiKey));
    }
    
    public RetrofitHttpClient(String apiKey, OkHttpClient customOkHttpClient) {
        this.okHttpClient = customOkHttpClient;
        
        // Single Retrofit instance for all APIs
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        
        this.apiService = retrofit.create(OneInchSwapApiService.class);
        this.tokenApiService = retrofit.create(OneInchTokenApiService.class);
        this.tokenDetailsApiService = retrofit.create(OneInchTokenDetailsApiService.class);
        this.orderbookApiService = retrofit.create(OneInchOrderbookApiService.class);
    }
    
    private static OkHttpClient createDefaultOkHttpClient(String apiKey) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(log::debug);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(apiKey))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    
    @Override
    public <T> Single<T> getRx(String path, Map<String, Object> params, Class<T> responseType) {
        log.debug("Making reactive GET request to: {}", path);
        
        // This is a simplified implementation - in practice, you'd need to map
        // the path and params to the appropriate API service method
        throw new UnsupportedOperationException("Direct path-based requests not supported with Retrofit. Use service methods instead.");
    }
    
    @Override
    public <T> T get(String path, Map<String, Object> params, Class<T> responseType) throws OneInchException {
        try {
            return getRx(path, params, responseType).blockingGet();
        } catch (Exception e) {
            throw new OneInchException("Request failed", e);
        }
    }
    
    @Override
    public <T> CompletableFuture<T> getAsync(String path, Map<String, Object> params, Class<T> responseType) {
        return getRx(path, params, responseType)
                .toCompletionStage()
                .toCompletableFuture();
    }
    
    public OneInchSwapApiService getSwapApiService() {
        return apiService;
    }
    
    public OneInchTokenApiService getTokenApiService() {
        return tokenApiService;
    }
    
    public OneInchTokenDetailsApiService getTokenDetailsApiService() {
        return tokenDetailsApiService;
    }
    
    public OneInchOrderbookApiService getOrderbookApiService() {
        return orderbookApiService;
    }
    
    @Override
    public void close() throws Exception {
        // OkHttp client resources will be cleaned up automatically
        okHttpClient.dispatcher().executorService().shutdown();
        okHttpClient.connectionPool().evictAll();
    }
    
    private static class AuthenticationInterceptor implements Interceptor {
        private final String apiKey;
        
        public AuthenticationInterceptor(String apiKey) {
            this.apiKey = apiKey;
        }
        
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .build();
            
            return chain.proceed(authenticatedRequest);
        }
    }
}