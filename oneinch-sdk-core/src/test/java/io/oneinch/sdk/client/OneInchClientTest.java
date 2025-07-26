package io.oneinch.sdk.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OneInchClientTest {
    

    @Test
    void testBuilderSuccess() {
        // For unit tests, we provide a test API key to avoid requiring real credentials
        OneInchClient client = OneInchClient.builder()
                .apiKey("test-api-key-for-unit-testing")
                .build();

        assertNotNull(client);
        assertNotNull(client.getHttpClient());
        assertNotNull(client.getSwapService());
        assertNotNull(client.swap());
        assertInstanceOf(RetrofitHttpClient.class, client.getHttpClient());
    }

}