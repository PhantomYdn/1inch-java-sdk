package io.oneinch.sdk.examples;

import io.oneinch.sdk.client.OneInchClient;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class HistoryConsoleExample {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'");
    private static final BigInteger WEI_PER_ETH = new BigInteger("1000000000000000000"); // 10^18

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        String address = args[0];
        Integer chainId = args.length > 1 ? parseInteger(args[1], "Chain ID") : 1; // Default to Ethereum
        Integer limit = args.length > 2 ? parseInteger(args[2], "Limit") : 10; // Default to 10 events

        if (chainId == null || limit == null) {
            System.exit(1);
        }

        // Validate address format
        if (!isValidEthereumAddress(address)) {
            System.err.println("❌ Error: Invalid Ethereum address format");
            System.err.println("   Address must be 42 characters long and start with '0x'");
            System.err.println("   Example: 0x111111111117dc0aa78b770fa6a738034120c302");
            System.exit(1);
        }

        HistoryConsoleExample example = new HistoryConsoleExample();
        example.displayTransactionHistory(address, chainId, limit);
    }

    /**
     * Main method to fetch and display transaction history
     */
    public void displayTransactionHistory(String address, Integer chainId, Integer limit) {
        try (OneInchClient client = OneInchClient.builder()
                .build()) { // Uses ONEINCH_API_KEY environment variable

            System.out.println("🔍 Fetching transaction history...");
            System.out.println();

            HistoryEventsRequest request = HistoryEventsRequest.builder()
                    .address(address)
                    .chainId(chainId)
                    .limit(limit)
                    .build();

            HistoryResponseDto response = client.history().getHistoryEvents(request);

            displayHeader(address, chainId, response.getItems().size());
            
            if (response.getItems().isEmpty()) {
                System.out.println("📭 No transaction history found for this address on the specified chain.");
                System.out.println("   This could mean:");
                System.out.println("   • The address has no transactions on this blockchain");
                System.out.println("   • The address is not yet indexed by the 1inch History API");
                System.out.println("   • Try a different chain ID (e.g., 1=Ethereum, 137=Polygon, 56=BSC)");
                return;
            }

            for (int i = 0; i < response.getItems().size(); i++) {
                HistoryEventDto event = response.getItems().get(i);
                displayEvent(i + 1, event);
                
                // Add separator between events (except for the last one)
                if (i < response.getItems().size() - 1) {
                    System.out.println("─".repeat(80));
                }
            }

            displayFooter(response.getItems().size());

        } catch (OneInchException e) {
            handleOneInchError(e);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Configuration Error: " + e.getMessage());
            System.err.println("   Please ensure ONEINCH_API_KEY environment variable is set.");
            System.err.println("   Get your API key from: https://portal.1inch.dev/");
        } catch (Exception e) {
            System.err.println("❌ Unexpected Error: " + e.getMessage());
            log.error("Unexpected error in history console example", e);
        }
    }

    /**
     * Display formatted header
     */
    private void displayHeader(String address, Integer chainId, int eventCount) {
        System.out.println("═".repeat(80));
        System.out.println("📜 TRANSACTION HISTORY");
        System.out.println("═".repeat(80));
        System.out.printf("Address: %s%n", address);
        System.out.printf("Chain:   %s (%d)%n", getChainName(chainId), chainId);
        System.out.printf("Events:  %d transaction%s found%n", eventCount, eventCount == 1 ? "" : "s");
        System.out.println("═".repeat(80));
        System.out.println();
    }

    /**
     * Display formatted event information
     */
    private void displayEvent(int eventNumber, HistoryEventDto event) {
        System.out.printf("📋 Event #%d [%s]%n", eventNumber, formatTimestamp(event.getTimeMs()));
        System.out.printf("   ID: %s | Type: %s | Rating: %s%n", 
                event.getId(), 
                event.getType(), 
                formatRating(event.getRating()));

        if (event.getDetails() != null) {
            TransactionDetailsDto details = event.getDetails();
            displayTransactionDetails(details);
            displayTokenActions(details.getTokenActions());
        }
        
        System.out.println();
    }

    /**
     * Display transaction details
     */
    private void displayTransactionDetails(TransactionDetailsDto details) {
        System.out.printf("   Transaction: %s%n", truncateHash(details.getTxHash()));
        System.out.printf("   Type: %s | Status: %s%n", 
                formatTransactionType(details.getType()), 
                formatTransactionStatus(details.getStatus()));
        System.out.printf("   Chain: %s (%d) | Block: %s%n", 
                getChainName(details.getChainId()), 
                details.getChainId(), 
                formatNumber(details.getBlockNumber()));
        
        if (details.getFeeInWei() != null) {
            System.out.printf("   Fee: %s ETH%n", formatWeiToEth(details.getFeeInWei()));
        }
    }

    /**
     * Display token actions in a readable format
     */
    private void displayTokenActions(List<TokenActionDto> tokenActions) {
        if (tokenActions == null || tokenActions.isEmpty()) {
            return;
        }

        System.out.println();
        System.out.println("   Token Actions:");
        for (TokenActionDto action : tokenActions) {
            String symbol = getTokenSymbol(action.getAddress());
            String direction = formatDirection(action.getDirection());
            String amount = formatTokenAmount(action.getAmount());
            String fromAddr = truncateAddress(action.getFromAddress());
            String toAddr = truncateAddress(action.getToAddress());

            System.out.printf("     %s %s %s (%s → %s)%n", 
                    direction, amount, symbol, fromAddr, toAddr);
        }
    }

    /**
     * Display footer
     */
    private void displayFooter(int eventCount) {
        System.out.println("═".repeat(80));
        System.out.printf("✅ Successfully displayed %d transaction event%s%n", 
                eventCount, eventCount == 1 ? "" : "s");
        System.out.println("═".repeat(80));
    }

    // ==================== FORMATTING HELPER METHODS ====================

    /**
     * Format timestamp from milliseconds to readable date
     */
    private String formatTimestamp(Long timestampMs) {
        if (timestampMs == null) return "Unknown";
        Instant instant = Instant.ofEpochMilli(timestampMs);
        return instant.atZone(ZoneOffset.UTC).format(DATE_FORMATTER);
    }

    /**
     * Format BigInteger wei amount to ETH with appropriate decimals
     */
    private String formatWeiToEth(BigInteger weiAmount) {
        if (weiAmount == null) return "0";
        BigDecimal eth = new BigDecimal(weiAmount).divide(new BigDecimal(WEI_PER_ETH), 6, RoundingMode.HALF_UP);
        return eth.stripTrailingZeros().toPlainString();
    }

    /**
     * Format token amount for display
     */
    private String formatTokenAmount(BigInteger amount) {
        if (amount == null) return "0";
        // For display purposes, assume 18 decimals for most tokens
        BigDecimal tokenAmount = new BigDecimal(amount).divide(new BigDecimal(WEI_PER_ETH), 4, RoundingMode.HALF_UP);
        return tokenAmount.stripTrailingZeros().toPlainString();
    }

    /**
     * Get token symbol from address (simplified)
     */
    private String getTokenSymbol(String address) {
        if (address == null) return "TOKEN";
        
        // Common token addresses
        switch (address.toLowerCase()) {
            case "0xeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee":
                return "ETH";
            case "0x111111111117dc0aa78b770fa6a738034120c302":
                return "1INCH";
            case "0xa0b86a33e6ab6b6ce4e5a5b7db2e8df6b1d2b9c7":
                return "USDC";
            case "0xdac17f958d2ee523a2206206994597c13d831ec7":
                return "USDT";
            default:
                return "TOKEN";
        }
    }

    /**
     * Format direction with appropriate symbol
     */
    private String formatDirection(String direction) {
        if (direction == null) return "?";
        switch (direction.toLowerCase()) {
            case "in": return "←";
            case "out": return "→";
            case "self": return "↔";
            case "on": return "●";
            default: return "?";
        }
    }

    /**
     * Format transaction type for display
     */
    private String formatTransactionType(String type) {
        if (type == null) return "Unknown";
        return type.replace("_", " ");
    }

    /**
     * Format transaction status with color-like indicators
     */
    private String formatTransactionStatus(String status) {
        if (status == null) return "Unknown";
        switch (status.toLowerCase()) {
            case "completed": return "✅ Completed";
            case "failed": return "❌ Failed";
            case "pending": return "⏳ Pending";
            case "cancelled":
            case "canceled": return "🚫 Cancelled";
            default: return status;
        }
    }

    /**
     * Format rating with appropriate emoji
     */
    private String formatRating(String rating) {
        if (rating == null) return "Unknown";
        switch (rating.toLowerCase()) {
            case "reliable": return "✅ Reliable";
            case "scam": return "⚠️ Scam";
            default: return rating;
        }
    }

    /**
     * Truncate transaction hash for display
     */
    private String truncateHash(String hash) {
        if (hash == null || hash.length() <= 10) return hash;
        return hash.substring(0, 6) + "..." + hash.substring(hash.length() - 4);
    }

    /**
     * Truncate address for display
     */
    private String truncateAddress(String address) {
        if (address == null || address.length() <= 10) return address;
        return address.substring(0, 6) + "..." + address.substring(address.length() - 4);
    }

    /**
     * Format large numbers with commas
     */
    private String formatNumber(Long number) {
        if (number == null) return "Unknown";
        return String.format("%,d", number);
    }

    /**
     * Get chain name from chain ID
     */
    private String getChainName(Integer chainId) {
        if (chainId == null) return "Unknown";
        switch (chainId) {
            case 1: return "Ethereum";
            case 56: return "BSC";
            case 137: return "Polygon";
            case 42161: return "Arbitrum";
            case 43114: return "Avalanche";
            case 10: return "Optimism";
            case 250: return "Fantom";
            case 100: return "Gnosis";
            default: return "Unknown";
        }
    }

    // ==================== VALIDATION AND UTILITY METHODS ====================

    /**
     * Validate Ethereum address format
     */
    private static boolean isValidEthereumAddress(String address) {
        if (address == null || !address.startsWith("0x") || address.length() != 42) {
            return false;
        }
        
        // Check if remaining characters are valid hex
        String hexPart = address.substring(2);
        return hexPart.matches("[0-9a-fA-F]+");
    }

    /**
     * Parse integer with error handling
     */
    private static Integer parseInteger(String value, String fieldName) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                System.err.printf("❌ Error: %s must be a positive integer, got: %s%n", fieldName, value);
                return null;
            }
            return parsed;
        } catch (NumberFormatException e) {
            System.err.printf("❌ Error: %s must be a valid integer, got: %s%n", fieldName, value);
            return null;
        }
    }

    /**
     * Handle 1inch API specific errors
     */
    private void handleOneInchError(OneInchException e) {
        System.err.println("❌ 1inch API Error: " + e.getMessage());
        
        if (e.getMessage().contains("Unauthorized") || e.getMessage().contains("401")) {
            System.err.println("   Please check your API key configuration:");
            System.err.println("   • Ensure ONEINCH_API_KEY environment variable is set");
            System.err.println("   • Verify your API key is valid and active");
            System.err.println("   • Get your API key from: https://portal.1inch.dev/");
        } else if (e.getMessage().contains("422") || e.getMessage().contains("Unprocessable")) {
            System.err.println("   This address might not have transaction history on the specified chain.");
            System.err.println("   Try:");
            System.err.println("   • Different chain ID (1=Ethereum, 137=Polygon, 56=BSC)");
            System.err.println("   • Verify the address has transactions on this blockchain");
        } else if (e.getMessage().contains("rate limit") || e.getMessage().contains("429")) {
            System.err.println("   Rate limit exceeded. Please wait a moment and try again.");
        }
    }

    /**
     * Print usage information
     */
    private static void printUsage() {
        System.out.println("📜 1inch Transaction History Console");
        System.out.println();
        System.out.println("Usage: HistoryConsoleExample <address> [chainId] [limit]");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  address  - Ethereum address (required)");
        System.out.println("             Example: 0x111111111117dc0aa78b770fa6a738034120c302");
        System.out.println("  chainId  - Blockchain network ID (optional, default: 1)");
        System.out.println("             1=Ethereum, 137=Polygon, 56=BSC, 42161=Arbitrum");
        System.out.println("  limit    - Number of events to fetch (optional, default: 10, max: 10000)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  # Get 10 recent events on Ethereum");
        System.out.println("  mvn exec:java -Dexec.args=\"0x111111111117dc0aa78b770fa6a738034120c302\"");
        System.out.println();
        System.out.println("  # Get 20 events on Polygon");
        System.out.println("  mvn exec:java -Dexec.args=\"0x742f4d5b7dbf2e4f0ddeadd3d1b4b8b4c1b8b8b8 137 20\"");
        System.out.println();
        System.out.println("Requirements:");
        System.out.println("  • Set ONEINCH_API_KEY environment variable");
        System.out.println("  • Get API key from: https://portal.1inch.dev/");
    }
}