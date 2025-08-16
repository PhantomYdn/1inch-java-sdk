package io.oneinch.mcp.resources;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.history.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MCP Resource for accessing transaction history data across blockchains.
 * Provides comprehensive transaction history with filtering and pagination capabilities.
 */
@ApplicationScoped
public class HistoryResource {

    private static final Logger log = LoggerFactory.getLogger(HistoryResource.class);

    @Inject
    OneInchIntegrationService integrationService;

    @Inject
    ApiResponseMapper responseMapper;

    /**
     * Get transaction history for a wallet address.
     * Format: /history/{address}
     */
    public CompletableFuture<String> getHistory(String address, Integer chainId, Integer limit) {
        log.info("Retrieving transaction history for address {} on chain {} limit {}", address, chainId, limit);
        
        HistoryEventsRequest request = HistoryEventsRequest.builder()
                .address(address)
                .chainId(chainId)
                .limit(limit != null ? limit : 20) // Default to 20 transactions
                .build();
        
        return integrationService.getHistory(request)
                .thenApply(response -> {
                    Map<String, Object> result = responseMapper.mapHistoryResponseDto(response);
                    return formatHistoryResponse(result, address, chainId, limit);
                })
                .exceptionally(throwable -> {
                    log.error("Error retrieving history for {}: {}", address, throwable.getMessage());
                    return formatErrorResponse("history_error", throwable.getMessage(), address);
                });
    }

    /**
     * Get transaction history filtered by token.
     * Format: /history/{address}/token/{tokenAddress}
     */
    public CompletableFuture<String> getHistoryByToken(String address, String tokenAddress, Integer chainId, Integer limit) {
        log.info("Retrieving token history for address {} token {} on chain {} limit {}", address, tokenAddress, chainId, limit);
        
        HistoryEventsRequest request = HistoryEventsRequest.builder()
                .address(address)
                .tokenAddress(tokenAddress)
                .chainId(chainId)
                .limit(limit != null ? limit : 20)
                .build();
        
        return integrationService.getHistory(request)
                .thenApply(response -> {
                    Map<String, Object> result = responseMapper.mapHistoryResponseDto(response);
                    return formatTokenHistoryResponse(result, address, tokenAddress, chainId, limit);
                })
                .exceptionally(throwable -> {
                    log.error("Error retrieving token history for {}:{}: {}", address, tokenAddress, throwable.getMessage());
                    return formatErrorResponse("token_history_error", throwable.getMessage(), address);
                });
    }

    /**
     * Get transaction history for a specific time range.
     * Format: /history/{address}/timerange
     */
    public CompletableFuture<String> getHistoryByTimeRange(String address, String fromTimestamp, String toTimestamp, 
                                                         Integer chainId, Integer limit) {
        log.info("Retrieving history for address {} time range {}:{} on chain {} limit {}", 
                address, fromTimestamp, toTimestamp, chainId, limit);
        
        HistoryEventsRequest request = HistoryEventsRequest.builder()
                .address(address)
                .fromTimestampMs(fromTimestamp)
                .toTimestampMs(toTimestamp)
                .chainId(chainId)
                .limit(limit != null ? limit : 50) // Larger default for time ranges
                .build();
        
        return integrationService.getHistory(request)
                .thenApply(response -> {
                    Map<String, Object> result = responseMapper.mapHistoryResponseDto(response);
                    return formatTimeRangeHistoryResponse(result, address, fromTimestamp, toTimestamp, chainId, limit);
                })
                .exceptionally(throwable -> {
                    log.error("Error retrieving time range history for {}: {}", address, throwable.getMessage());
                    return formatErrorResponse("time_range_history_error", throwable.getMessage(), address);
                });
    }

    /**
     * Get recent transaction history across all chains.
     * Format: /history/{address}/recent
     */
    public CompletableFuture<String> getRecentHistory(String address, Integer limit) {
        log.info("Retrieving recent history for address {} limit {}", address, limit);
        
        // Get recent history without chain filter
        HistoryEventsRequest request = HistoryEventsRequest.builder()
                .address(address)
                .limit(limit != null ? limit : 10) // Smaller default for recent
                .build();
        
        return integrationService.getHistory(request)
                .thenApply(response -> {
                    Map<String, Object> result = responseMapper.mapHistoryResponseDto(response);
                    return formatRecentHistoryResponse(result, address, limit);
                })
                .exceptionally(throwable -> {
                    log.error("Error retrieving recent history for {}: {}", address, throwable.getMessage());
                    return formatErrorResponse("recent_history_error", throwable.getMessage(), address);
                });
    }

    /**
     * Get transaction statistics summary for an address.
     * Format: /history/{address}/summary
     */
    public CompletableFuture<String> getHistorySummary(String address, Integer chainId) {
        log.info("Retrieving history summary for address {} on chain {}", address, chainId);
        
        // Get larger dataset for analysis
        HistoryEventsRequest request = HistoryEventsRequest.builder()
                .address(address)
                .chainId(chainId)
                .limit(100) // Get more data for analysis
                .build();
        
        return integrationService.getHistory(request)
                .thenApply(response -> {
                    Map<String, Object> result = responseMapper.mapHistoryResponseDto(response);
                    return formatHistorySummaryResponse(result, address, chainId);
                })
                .exceptionally(throwable -> {
                    log.error("Error retrieving history summary for {}: {}", address, throwable.getMessage());
                    return formatErrorResponse("history_summary_error", throwable.getMessage(), address);
                });
    }

    // === RESPONSE FORMATTING METHODS ===

    private String formatHistoryResponse(Map<String, Object> result, String address, Integer chainId, Integer limit) {
        return String.format(
            "{" +
            "\"resource\": \"history/%s\"," +
            "\"type\": \"transaction_history\"," +
            "\"address\": \"%s\"," +
            "\"chain_filter\": %s," +
            "\"limit\": %d," +
            "\"total_transactions\": %d," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            address, address,
            chainId != null ? "\"" + chainId + "\"" : "null",
            limit != null ? limit : 20,
            result.containsKey("items") ? ((List<?>) result.get("items")).size() : 0,
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatTokenHistoryResponse(Map<String, Object> result, String address, String tokenAddress, 
                                            Integer chainId, Integer limit) {
        return String.format(
            "{" +
            "\"resource\": \"history/%s/token/%s\"," +
            "\"type\": \"token_history\"," +
            "\"address\": \"%s\"," +
            "\"token_address\": \"%s\"," +
            "\"chain_filter\": %s," +
            "\"limit\": %d," +
            "\"total_transactions\": %d," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            address, tokenAddress, address, tokenAddress,
            chainId != null ? "\"" + chainId + "\"" : "null",
            limit != null ? limit : 20,
            result.containsKey("items") ? ((List<?>) result.get("items")).size() : 0,
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatTimeRangeHistoryResponse(Map<String, Object> result, String address, String fromTimestamp, 
                                                String toTimestamp, Integer chainId, Integer limit) {
        return String.format(
            "{" +
            "\"resource\": \"history/%s/timerange\"," +
            "\"type\": \"time_range_history\"," +
            "\"address\": \"%s\"," +
            "\"from_timestamp\": \"%s\"," +
            "\"to_timestamp\": \"%s\"," +
            "\"chain_filter\": %s," +
            "\"limit\": %d," +
            "\"total_transactions\": %d," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            address, address, fromTimestamp, toTimestamp,
            chainId != null ? "\"" + chainId + "\"" : "null",
            limit != null ? limit : 50,
            result.containsKey("items") ? ((List<?>) result.get("items")).size() : 0,
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatRecentHistoryResponse(Map<String, Object> result, String address, Integer limit) {
        return String.format(
            "{" +
            "\"resource\": \"history/%s/recent\"," +
            "\"type\": \"recent_history\"," +
            "\"address\": \"%s\"," +
            "\"limit\": %d," +
            "\"total_transactions\": %d," +
            "\"chains_involved\": %s," +
            "\"data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            address, address,
            limit != null ? limit : 10,
            result.containsKey("items") ? ((List<?>) result.get("items")).size() : 0,
            extractChainsFromHistory(result),
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatHistorySummaryResponse(Map<String, Object> result, String address, Integer chainId) {
        Map<String, Object> summary = generateSummaryStats(result);
        
        return String.format(
            "{" +
            "\"resource\": \"history/%s/summary\"," +
            "\"type\": \"history_summary\"," +
            "\"address\": \"%s\"," +
            "\"chain_filter\": %s," +
            "\"analysis\": %s," +
            "\"raw_data\": %s," +
            "\"timestamp\": %d" +
            "}", 
            address, address,
            chainId != null ? "\"" + chainId + "\"" : "null",
            toJsonString(summary),
            toJsonString(result),
            System.currentTimeMillis()
        );
    }

    private String formatErrorResponse(String errorType, String message, String resource) {
        return String.format(
            "{" +
            "\"resource\": \"history/%s\"," +
            "\"type\": \"error\"," +
            "\"error\": {" +
                "\"type\": \"%s\"," +
                "\"message\": \"%s\"," +
                "\"timestamp\": %d" +
            "}" +
            "}", 
            resource, errorType, message.replace("\"", "\\\""), System.currentTimeMillis()
        );
    }

    // === UTILITY METHODS ===

    private String extractChainsFromHistory(Map<String, Object> result) {
        // Extract unique chain IDs from transaction history
        if (result.containsKey("items") && result.get("items") instanceof List) {
            List<?> items = (List<?>) result.get("items");
            return "[" + items.stream()
                    .map(item -> {
                        if (item instanceof Map) {
                            Map<?, ?> itemMap = (Map<?, ?>) item;
                            Object details = itemMap.get("details");
                            if (details instanceof Map) {
                                Map<?, ?> detailsMap = (Map<?, ?>) details;
                                Object chainId = detailsMap.get("chainId");
                                return chainId != null ? chainId.toString() : "unknown";
                            }
                        }
                        return "unknown";
                    })
                    .distinct()
                    .collect(Collectors.joining(",")) + "]";
        }
        return "[]";
    }

    private Map<String, Object> generateSummaryStats(Map<String, Object> result) {
        Map<String, Object> summary = new java.util.HashMap<>();
        
        if (result.containsKey("items") && result.get("items") instanceof List) {
            List<?> items = (List<?>) result.get("items");
            summary.put("total_transactions", items.size());
            
            // Count transaction types
            Map<String, Long> typeCount = items.stream()
                    .map(item -> {
                        if (item instanceof Map) {
                            Map<?, ?> itemMap = (Map<?, ?>) item;
                            Object type = itemMap.get("type");
                            return type != null ? type.toString() : "unknown";
                        }
                        return "unknown";
                    })
                    .collect(Collectors.groupingBy(type -> type, Collectors.counting()));
            summary.put("transaction_types", typeCount);
            
            // Count unique chains
            long uniqueChains = items.stream()
                    .map(item -> {
                        if (item instanceof Map) {
                            Map<?, ?> itemMap = (Map<?, ?>) item;
                            Object details = itemMap.get("details");
                            if (details instanceof Map) {
                                Map<?, ?> detailsMap = (Map<?, ?>) details;
                                Object chainId = detailsMap.get("chainId");
                                return chainId != null ? chainId.toString() : "unknown";
                            }
                        }
                        return "unknown";
                    })
                    .distinct()
                    .count();
            summary.put("unique_chains", uniqueChains);
        } else {
            summary.put("total_transactions", 0);
            summary.put("transaction_types", new java.util.HashMap<>());
            summary.put("unique_chains", 0);
        }
        
        return summary;
    }

    private String toJsonString(Object obj) {
        // Simple JSON serialization - in production, use proper JSON library
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + ((String) obj).replace("\"", "\\\"") + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof List) return "[" + ((List<?>) obj).stream()
                .map(this::toJsonString)
                .collect(Collectors.joining(",")) + "]";
        if (obj instanceof Map) {
            return "{" + ((Map<?, ?>) obj).entrySet().stream()
                    .map(entry -> "\"" + entry.getKey() + "\":" + toJsonString(entry.getValue()))
                    .collect(Collectors.joining(",")) + "}";
        }
        return "\"" + obj.toString() + "\"";
    }
}