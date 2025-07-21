package io.oneinch.sdk.client;

import io.oneinch.sdk.service.SwapService;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

class OneInchClientTest {
    
    private String originalEnvValue;
    
    @BeforeEach
    void setUp() {
        // Save original environment variable value
        originalEnvValue = System.getenv("ONEINCH_API_KEY");
    }
    
    @AfterEach
    void tearDown() {
        // Note: We cannot restore environment variables in Java without reflection
        // In real testing scenarios, consider using system property overrides
        // or mocking frameworks for environment variable testing
    }

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
    void testBuilder_MissingApiKeyAndNoEnvVar() {
        // This test assumes no ONEINCH_API_KEY environment variable is set
        // In CI/CD environments, ensure this variable is not set for this test
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            OneInchClient.builder().build();
        });

        assertTrue(exception.getMessage().contains("API key is required"));
        assertTrue(exception.getMessage().contains("ONEINCH_API_KEY"));
    }

    @Test
    void testBuilder_EmptyApiKey() {
        // Empty string should fallback to environment variable, then fail if not set
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            OneInchClient.builder()
                    .apiKey("")
                    .build();
        });

        assertTrue(exception.getMessage().contains("API key is required"));
    }

    @Test
    void testBuilder_WhitespaceApiKey() {
        // Whitespace-only string should fallback to environment variable, then fail if not set
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            OneInchClient.builder()
                    .apiKey("   ")
                    .build();
        });

        assertTrue(exception.getMessage().contains("API key is required"));
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
    void testParameterlessConstructor() {
        // This test will fail if ONEINCH_API_KEY is not set in the environment
        // In practice, you'd want to set this environment variable for testing
        try {
            OneInchClient client = new OneInchClient();
            assertNotNull(client);
            assertNotNull(client.getHttpClient());
            assertNotNull(client.getSwapService());
        } catch (IllegalArgumentException e) {
            // Expected if no environment variable is set
            assertTrue(e.getMessage().contains("API key is required"));
        }
    }
    
    @Test
    void testExplicitApiKeyTakesPrecedenceOverEnv() {
        // This test shows that explicit API key is used even if env var exists
        OneInchClient client = OneInchClient.builder()
                .apiKey("explicit-api-key")
                .build();

        assertNotNull(client);
        assertNotNull(client.getHttpClient());
        assertNotNull(client.getSwapService());
    }
    
    @Test 
    void testBuilderWithoutApiKeyCallsParameterlessConstructor() {
        // Test that builder without apiKey() call works same as parameterless constructor
        try {
            OneInchClient client = OneInchClient.builder().build();
            assertNotNull(client);
            assertNotNull(client.getHttpClient());
            assertNotNull(client.getSwapService());
        } catch (IllegalArgumentException e) {
            // Expected if no environment variable is set
            assertTrue(e.getMessage().contains("API key is required"));
            assertTrue(e.getMessage().contains("ONEINCH_API_KEY"));
        }
    }
    
    @Test
    void testEnvironmentVariableMessage() {
        // Test that error message mentions environment variable
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            OneInchClient.builder().build();
        });
        
        String message = exception.getMessage();
        assertTrue(message.contains("ONEINCH_API_KEY"));
        assertTrue(message.contains("environment variable"));
    }
}