package io.oneinch.mcp;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;

/**
 * Basic test for the MCP server application startup and health checks.
 */
@QuarkusTest
class OneInchMcpApplicationTest {

    @Test
    void testApplicationStartup() {
        // Test that the application starts successfully
        // This test will pass if Quarkus can start the application context
    }

    @Test
    void testHealthEndpoint() {
        RestAssured.when()
                .get("/health/ready")
                .then()
                .statusCode(200)
                .body(containsString("1inch-sdk-client"));
    }

    @Test
    void testLivenessEndpoint() {
        RestAssured.when()
                .get("/health/live")
                .then()
                .statusCode(200);
    }
}