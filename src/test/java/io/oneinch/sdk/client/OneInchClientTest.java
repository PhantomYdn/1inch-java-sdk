package io.oneinch.sdk.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OneInchClientTest {
    

    @Test
    void testBuilderSuccess() {
        System.out.println("Testing OneInchClient builder...");
        System.out.println("Properties:");
        System.getProperties().forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println("Env:");
        System.getenv().forEach((key, value) -> System.out.println(key + ": " + value));
        OneInchClient client = OneInchClient.builder().build();

        assertNotNull(client);
        assertNotNull(client.getHttpClient());
        assertNotNull(client.getSwapService());
        assertNotNull(client.swap());
        assertInstanceOf(RetrofitHttpClient.class, client.getHttpClient());
    }

}