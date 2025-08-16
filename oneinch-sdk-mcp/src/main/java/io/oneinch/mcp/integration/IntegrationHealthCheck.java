package io.oneinch.mcp.integration;

import io.oneinch.mcp.service.OneInchClientService;
import io.oneinch.sdk.model.swap.QuoteRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Health check for 1inch SDK integration.
 * Performs basic connectivity tests to ensure the integration is working.
 */
@Readiness
@ApplicationScoped
public class IntegrationHealthCheck implements HealthCheck {

    private static final Logger log = LoggerFactory.getLogger(IntegrationHealthCheck.class);

    @Inject
    OneInchClientService clientService;

    @Inject
    OneInchIntegrationService integrationService;

    @Override
    public HealthCheckResponse call() {
        try {
            // Check if client service is ready
            if (!clientService.isReady()) {
                return HealthCheckResponse.named("oneinch-integration")
                        .down()
                        .withData("status", "client_not_ready")
                        .withData("description", "1inch SDK client is not initialized")
                        .build();
            }

            // Perform a basic API connectivity test
            boolean apiConnectable = testApiConnectivity();
            
            if (apiConnectable) {
                return HealthCheckResponse.named("oneinch-integration")
                        .up()
                        .withData("status", "ready")
                        .withData("description", "1inch SDK integration operational")
                        .withData("features", "swap, token, price, portfolio, balance, history APIs")
                        .build();
            } else {
                return HealthCheckResponse.named("oneinch-integration")
                        .down()
                        .withData("status", "api_unreachable")
                        .withData("description", "Cannot connect to 1inch APIs")
                        .build();
            }

        } catch (Exception e) {
            log.error("Error during integration health check", e);
            return HealthCheckResponse.named("oneinch-integration")
                    .down()
                    .withData("status", "error")
                    .withData("error", e.getMessage())
                    .build();
        }
    }

    /**
     * Tests basic API connectivity by attempting a simple operation.
     */
    private boolean testApiConnectivity() {
        try {
            // Create a minimal test quote request
            QuoteRequest testRequest = QuoteRequest.builder()
                    .chainId(1) // Ethereum
                    .src("0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee") // ETH
                    .dst("0x111111111117dc0aa78b770fa6a738034120c302") // 1INCH
                    .amount(new BigInteger("1000000000000000")) // 0.001 ETH
                    .build();

            // Attempt to get a quote (this tests the full integration chain)
            integrationService.getSwapQuote(testRequest)
                    .get(java.util.concurrent.TimeUnit.SECONDS.toMillis(5),
                         java.util.concurrent.TimeUnit.MILLISECONDS);
            
            log.debug("API connectivity test successful");
            return true;

        } catch (Exception e) {
            log.debug("API connectivity test failed: {}", e.getMessage());
            return false;
        }
    }
}