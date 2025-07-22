package io.oneinch.sdk.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OneInchClientTest {
    

    @Test
    void testBuilderSuccess() {
        OneInchClient client = OneInchClient.builder().build();

        assertNotNull(client);
        assertNotNull(client.getHttpClient());
        assertNotNull(client.getSwapService());
        assertNotNull(client.swap());
        assertInstanceOf(RetrofitHttpClient.class, client.getHttpClient());
    }

}