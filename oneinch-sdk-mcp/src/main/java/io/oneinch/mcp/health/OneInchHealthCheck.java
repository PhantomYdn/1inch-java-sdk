package io.oneinch.mcp.health;

import io.oneinch.mcp.service.OneInchClientService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

/**
 * Health check for the 1inch SDK client connectivity.
 * Verifies that the MCP server can communicate with 1inch APIs.
 */
@Readiness
@ApplicationScoped
public class OneInchHealthCheck implements HealthCheck {

    @Inject
    OneInchClientService clientService;

    @Override
    public HealthCheckResponse call() {
        try {
            if (!clientService.isReady()) {
                return HealthCheckResponse.named("1inch-sdk-client")
                        .down()
                        .withData("status", "not_initialized")
                        .build();
            }

            // Basic connectivity check - client is initialized and ready
            return HealthCheckResponse.named("1inch-sdk-client")
                    .up()
                    .withData("status", "ready")
                    .withData("description", "1inch SDK client is properly initialized")
                    .build();

        } catch (Exception e) {
            return HealthCheckResponse.named("1inch-sdk-client")
                    .down()
                    .withData("status", "error")
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}