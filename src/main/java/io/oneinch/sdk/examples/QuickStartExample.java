package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.QuoteRequest;
import io.oneinch.sdk.model.QuoteResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuickStartExample {

    public static void main(String[] args) {
        String apiKey = "YOUR_API_KEY_HERE";
        
        try (OneInchClient client = OneInchClient.builder()
                .apiKey(apiKey)
                .build()) {
            
            // Get a quote for swapping ETH to 1INCH
            QuoteRequest quoteRequest = QuoteRequest.builder()
                    .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee")  // ETH
                    .dst("0x111111111117dc0aa78b770fa6a738034120c302")  // 1INCH
                    .amount("10000000000000000")  // 0.01 ETH in wei
                    .build();

            QuoteResponse quote = client.swap().getQuote(quoteRequest);
            log.info("Expected output: {} 1INCH tokens", quote.getDstAmount());
            
        } catch (OneInchException e) {
            log.error("Error getting quote: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error", e);
        }
    }
}