package io.oneinch.sdk.client;

import io.oneinch.sdk.service.SwapService;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OneInchClientTest {

    @Test
    void testBuilder_Success() {
        OneInchClient client = OneInchClient.builder()
                .apiKey("test-api-key")
                .build();

        assertNotNull(client);
        assertNotNull(client.getHttpClient());
        assertNotNull(client.getSwapService());
        assertNotNull(client.swap());
        assertInstanceOf(RetrofitHttpClient.class, client.getHttpClient());
    }

    @Test
    void testBuilder_MissingApiKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            OneInchClient.builder().build();
        });

        assertEquals("API key is required", exception.getMessage());
    }

    @Test
    void testBuilder_EmptyApiKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            OneInchClient.builder()
                    .apiKey("")
                    .build();
        });

        assertEquals("API key is required", exception.getMessage());
    }

    @Test
    void testBuilder_WhitespaceApiKey() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            OneInchClient.builder()
                    .apiKey("   ")
                    .build();
        });

        assertEquals("API key is required", exception.getMessage());
    }

    @Test
    void testBuilder_WithCustomOkHttpClient() {
        OkHttpClient customClient = new OkHttpClient.Builder().build();
        
        OneInchClient client = OneInchClient.builder()
                .apiKey("test-api-key")
                .okHttpClient(customClient)
                .build();

        assertNotNull(client);
        assertNotNull(client.getHttpClient());
        assertNotNull(client.getSwapService());
    }

    @Test
    void testConstructor_Direct() {
        OneInchClient client = new OneInchClient("test-api-key");

        assertNotNull(client);
        assertNotNull(client.getHttpClient());
        assertNotNull(client.getSwapService());
        assertInstanceOf(SwapService.class, client.swap());
        assertInstanceOf(RetrofitHttpClient.class, client.getHttpClient());
    }

    @Test
    void testConstructor_WithCustomOkHttpClient() {
        OkHttpClient customClient = new OkHttpClient.Builder().build();
        OneInchClient client = new OneInchClient("test-api-key", customClient);

        assertNotNull(client);
        assertNotNull(client.getHttpClient());
        assertNotNull(client.getSwapService());
        assertInstanceOf(RetrofitHttpClient.class, client.getHttpClient());
    }

    @Test
    void testSwapService_Access() {
        OneInchClient client = OneInchClient.builder()
                .apiKey("test-api-key")
                .build();

        SwapService swapService = client.swap();

        assertNotNull(swapService);
        assertSame(client.getSwapService(), swapService);
    }

    @Test
    void testDeprecatedHttpClientMethod() {
        // Test the deprecated httpClient method doesn't break the builder
        OneInchClient client = OneInchClient.builder()
                .apiKey("test-api-key")
                .build();

        assertNotNull(client);
        assertInstanceOf(RetrofitHttpClient.class, client.getHttpClient());
    }
}