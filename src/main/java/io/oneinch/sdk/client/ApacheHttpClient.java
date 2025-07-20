package io.oneinch.sdk.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oneinch.sdk.exception.OneInchApiException;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.QuoteRequestError;
import io.oneinch.sdk.model.SwapRequestError;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ApacheHttpClient implements HttpClient {
    
    private static final String BASE_URL = "https://api.1inch.dev/swap/v6.0/1";
    
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final ExecutorService executor;
    
    public ApacheHttpClient(String apiKey) {
        this(apiKey, HttpClients.createDefault());
    }
    
    public ApacheHttpClient(String apiKey, CloseableHttpClient httpClient) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        this.executor = Executors.newCachedThreadPool();
    }
    
    @Override
    public <T> T get(String path, Map<String, Object> params, Class<T> responseType) throws OneInchException {
        try {
            URIBuilder uriBuilder = new URIBuilder(BASE_URL + path);
            
            if (params != null) {
                params.forEach((key, value) -> {
                    if (value != null) {
                        uriBuilder.addParameter(key, value.toString());
                    }
                });
            }
            
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Authorization", "Bearer " + apiKey);
            httpGet.setHeader("Content-Type", "application/json");
            
            log.debug("Making GET request to: {}", httpGet.getURI());
            
            HttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity());
            
            log.debug("Response status: {}, body: {}", response.getStatusLine().getStatusCode(), responseBody);
            
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return objectMapper.readValue(responseBody, responseType);
            } else {
                handleErrorResponse(responseBody, response.getStatusLine().getStatusCode());
                return null;
            }
            
        } catch (URISyntaxException | IOException e) {
            throw new OneInchException("HTTP request failed", e);
        }
    }
    
    @Override
    public <T> CompletableFuture<T> getAsync(String path, Map<String, Object> params, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return get(path, params, responseType);
            } catch (OneInchException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
    
    private void handleErrorResponse(String responseBody, int statusCode) throws OneInchException {
        try {
            if (statusCode == HttpStatus.SC_BAD_REQUEST) {
                try {
                    QuoteRequestError error = objectMapper.readValue(responseBody, QuoteRequestError.class);
                    throw new OneInchApiException(error.getError(), error.getDescription(), 
                        error.getStatusCode(), error.getRequestId(), error.getMeta());
                } catch (Exception e) {
                    try {
                        SwapRequestError error = objectMapper.readValue(responseBody, SwapRequestError.class);
                        throw new OneInchApiException(error.getError(), error.getDescription(), 
                            error.getStatusCode(), error.getRequestId(), error.getMeta());
                    } catch (Exception ex) {
                        throw new OneInchException("Unknown API error: " + responseBody);
                    }
                }
            }
            throw new OneInchException("HTTP " + statusCode + ": " + responseBody);
        } catch (OneInchApiException e) {
            throw e;
        } catch (Exception e) {
            throw new OneInchException("Failed to parse error response", e);
        }
    }
    
    @Override
    public void close() throws Exception {
        executor.shutdown();
        httpClient.close();
    }
}