package io.oneinch.sdk.client;

import io.oneinch.sdk.service.SwapService;
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
    void testConstructor_Direct() {
        OneInchClient client = new OneInchClient("test-api-key");

        assertNotNull(client);
        assertNotNull(client.getHttpClient());
        assertNotNull(client.getSwapService());
        assertInstanceOf(SwapService.class, client.swap());
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
}