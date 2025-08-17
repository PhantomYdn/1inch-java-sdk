package io.oneinch.mcp.config;

import io.oneinch.mcp.prompts.DeFiPrompts;
import io.oneinch.mcp.resources.*;
import io.oneinch.mcp.tools.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MCP Registration service that registers all resources, tools, and prompts
 * with the MCP server framework. This class serves as the central registry
 * for all MCP components in the 1inch SDK MCP Server.
 */
@ApplicationScoped
public class McpRegistration {

    private static final Logger log = LoggerFactory.getLogger(McpRegistration.class);

    // === RESOURCES ===
    @Inject
    TokenResource tokenResource;

    @Inject
    PriceResource priceResource;

    @Inject
    PortfolioResource portfolioResource;

    @Inject
    BalanceResource balanceResource;

    @Inject
    HistoryResource historyResource;

    @Inject
    SwapRouteResource swapRouteResource;

    // === TOOLS ===
    @Inject
    SwapQuoteTool swapQuoteTool;

    @Inject
    TokenAnalysisTool tokenAnalysisTool;

    @Inject
    PortfolioValueTool portfolioValueTool;

    @Inject
    SearchTokensTool searchTokensTool;

    @Inject
    GasOptimizationTool gasOptimizationTool;

    @Inject
    MarketTrendsTool marketTrendsTool;

    // === PROMPTS ===
    @Inject
    DeFiPrompts deFiPrompts;

    /**
     * Register all MCP resources with their endpoints and descriptions.
     * 
     * @return List of registered resources
     */
    public List<McpResourceRegistration> registerResources() {
        List<McpResourceRegistration> resources = new ArrayList<>();
        
        // Token Resources
        resources.add(new McpResourceRegistration(
            "tokens/{chainId}",
            "Token lists and metadata for specific blockchain networks",
            "application/json",
            new String[]{"chainId"},
            "Get comprehensive token information including symbols, names, addresses, and metadata for a specific chain"
        ));

        // Price Resources
        resources.add(new McpResourceRegistration(
            "prices/{chainId}/{token}",
            "Real-time token pricing data across multiple currencies",
            "application/json",
            new String[]{"chainId", "token"},
            "Get current price data for tokens with support for 60+ fiat and crypto currencies"
        ));

        // Portfolio Resources
        resources.add(new McpResourceRegistration(
            "portfolio/{address}",
            "DeFi portfolio snapshots and analytics across multiple chains",
            "application/json",
            new String[]{"address"},
            "Comprehensive portfolio analysis including positions, P&L, and protocol breakdown"
        ));

        // Balance Resources
        resources.add(new McpResourceRegistration(
            "balances/{chainId}/{address}",
            "Token balances and allowances for wallet addresses",
            "application/json",
            new String[]{"chainId", "address"},
            "Get token balances, allowances, and comprehensive wallet analysis"
        ));

        // History Resources
        resources.add(new McpResourceRegistration(
            "history/{address}",
            "Transaction history and analytics across multiple chains",
            "application/json",
            new String[]{"address"},
            "Detailed transaction history with categorization and analytics"
        ));

        // Swap Route Resources
        resources.add(new McpResourceRegistration(
            "swap-routes/{chainId}",
            "Optimal swap routes and quotes for token exchanges",
            "application/json",
            new String[]{"chainId"},
            "Best swap routes with pricing, gas estimates, and route analysis"
        ));

        log.info("Registered {} MCP resources", resources.size());
        return resources;
    }

    /**
     * Register all MCP tools with their functions and schemas.
     * 
     * @return List of registered tools
     */
    public List<McpToolRegistration> registerTools() {
        List<McpToolRegistration> tools = new ArrayList<>();

        // Swap Quote Tool
        tools.add(new McpToolRegistration(
            "getSwapQuote",
            "Generate swap quotes with comprehensive route analysis",
            createSwapQuoteSchema(),
            "Analyze optimal swap paths, calculate slippage, and provide detailed quote information with route optimization"
        ));

        // Token Analysis Tool
        tools.add(new McpToolRegistration(
            "analyzeToken",
            "Comprehensive token analysis with market metrics",
            createTokenAnalysisSchema(),
            "Detailed token analysis including price data, market metrics, risk assessment, and cross-chain comparison"
        ));

        // Portfolio Value Tool
        tools.add(new McpToolRegistration(
            "getPortfolioValue",
            "Portfolio valuation and performance metrics",
            createPortfolioValueSchema(),
            "Complete portfolio analysis with P&L metrics, risk assessment, and optimization recommendations"
        ));

        // Search Tokens Tool
        tools.add(new McpToolRegistration(
            "searchTokens",
            "Multi-chain token search and discovery",
            createSearchTokensSchema(),
            "Advanced token search across multiple chains with filtering, trending analysis, and cross-chain price comparison"
        ));

        // Gas Optimization Tool
        tools.add(new McpToolRegistration(
            "analyzeGasOptimization",
            "Gas cost optimization and timing recommendations",
            createGasOptimizationSchema(),
            "Gas fee analysis with optimization strategies, timing recommendations, and cross-chain cost comparison"
        ));

        // Market Trends Tool
        tools.add(new McpToolRegistration(
            "analyzeMarketTrends",
            "Market trend analysis and sentiment tracking",
            createMarketTrendsSchema(),
            "Comprehensive market analysis including trends, sentiment, opportunities, and cross-chain performance"
        ));

        log.info("Registered {} MCP tools", tools.size());
        return tools;
    }

    /**
     * Register all MCP prompts with their templates and parameters.
     * 
     * @return List of registered prompts
     */
    public List<McpPromptRegistration> registerPrompts() {
        List<McpPromptRegistration> prompts = new ArrayList<>();

        // Get available prompts from DeFiPrompts service
        Map<String, String> availablePrompts = deFiPrompts.getAvailablePrompts();
        
        for (Map.Entry<String, String> entry : availablePrompts.entrySet()) {
            prompts.add(new McpPromptRegistration(
                entry.getKey(),
                entry.getValue(),
                createPromptSchema(entry.getKey()),
                "DeFi analysis template optimized for AI model consumption"
            ));
        }

        log.info("Registered {} MCP prompts", prompts.size());
        return prompts;
    }

    /**
     * Get complete MCP server registration information.
     * 
     * @return Complete MCP registration data
     */
    public McpServerRegistration getCompleteRegistration() {
        return new McpServerRegistration(
            registerResources(),
            registerTools(),
            registerPrompts(),
            createServerInfo()
        );
    }

    // === SCHEMA CREATION METHODS ===

    private Map<String, Object> createSwapQuoteSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("description", "Parameters for swap quote generation");
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("chainId", createParameter("integer", "Blockchain network ID (1=Ethereum, 137=Polygon, etc.)", true));
        properties.put("srcToken", createParameter("string", "Source token address", true));
        properties.put("dstToken", createParameter("string", "Destination token address", true));
        properties.put("amount", createParameter("string", "Amount to swap in wei (string format)", true));
        properties.put("protocols", createParameter("string", "Optional comma-separated list of protocols", false));
        properties.put("parts", createParameter("integer", "Optional number of parts for routing", false));
        properties.put("fee", createParameter("number", "Optional fee percentage (0.0-3.0)", false));
        
        schema.put("properties", properties);
        schema.put("required", new String[]{"chainId", "srcToken", "dstToken", "amount"});
        
        return schema;
    }

    private Map<String, Object> createTokenAnalysisSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("description", "Parameters for comprehensive token analysis");
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("chainId", createParameter("integer", "Blockchain network ID", true));
        properties.put("tokenAddress", createParameter("string", "Token contract address", true));
        properties.put("currency", createParameter("string", "Price currency (USD, EUR, etc.)", false));
        properties.put("includeMetrics", createParameter("boolean", "Include advanced metrics", false));
        
        schema.put("properties", properties);
        schema.put("required", new String[]{"chainId", "tokenAddress"});
        
        return schema;
    }

    private Map<String, Object> createPortfolioValueSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("description", "Parameters for portfolio valuation analysis");
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("addresses", createArrayParameter("string", "Array of wallet addresses", true));
        properties.put("chainId", createParameter("integer", "Optional specific chain ID", false));
        properties.put("includeMetrics", createParameter("boolean", "Include detailed metrics", false));
        
        schema.put("properties", properties);
        schema.put("required", new String[]{"addresses"});
        
        return schema;
    }

    private Map<String, Object> createSearchTokensSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("description", "Parameters for multi-chain token search");
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("query", createParameter("string", "Search query (symbol, name, or address)", true));
        properties.put("chainIds", createArrayParameter("integer", "Array of chain IDs to search", false));
        properties.put("limit", createParameter("integer", "Maximum results per chain", false));
        properties.put("includeMetrics", createParameter("boolean", "Include market metrics", false));
        
        schema.put("properties", properties);
        schema.put("required", new String[]{"query"});
        
        return schema;
    }

    private Map<String, Object> createGasOptimizationSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("description", "Parameters for gas optimization analysis");
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("chainId", createParameter("integer", "Blockchain network ID", true));
        properties.put("operationType", createParameter("string", "Type of operation (swap, approve, transfer, etc.)", true));
        properties.put("urgency", createParameter("string", "Urgency level (low, medium, high)", false));
        properties.put("amount", createParameter("string", "Optional transaction amount", false));
        
        schema.put("properties", properties);
        schema.put("required", new String[]{"chainId", "operationType"});
        
        return schema;
    }

    private Map<String, Object> createMarketTrendsSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("description", "Parameters for market trends analysis");
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("tokenAddresses", createArrayParameter("string", "Array of token addresses", true));
        properties.put("chainId", createParameter("integer", "Blockchain network ID", true));
        properties.put("timeframe", createParameter("string", "Analysis timeframe (1h, 24h, 7d, etc.)", false));
        properties.put("includeVolume", createParameter("boolean", "Include volume analysis", false));
        
        schema.put("properties", properties);
        schema.put("required", new String[]{"tokenAddresses", "chainId"});
        
        return schema;
    }

    private Map<String, Object> createPromptSchema(String promptName) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("description", "Parameters for " + promptName + " prompt template");
        
        Map<String, Object> properties = new HashMap<>();
        
        // Add specific properties based on prompt type
        switch (promptName) {
            case "swap-analysis":
                properties.put("srcToken", createParameter("string", "Source token symbol or address", true));
                properties.put("dstToken", createParameter("string", "Destination token symbol or address", true));
                properties.put("amount", createParameter("string", "Amount to swap", true));
                properties.put("chainId", createParameter("integer", "Optional chain ID", false));
                break;
            case "portfolio-review":
                properties.put("address", createParameter("string", "Wallet address to analyze", true));
                properties.put("timeframe", createParameter("string", "Analysis timeframe", false));
                properties.put("chainId", createParameter("integer", "Optional specific chain", false));
                break;
            case "token-research":
                properties.put("token", createParameter("string", "Token symbol or address", true));
                properties.put("chainId", createParameter("integer", "Chain ID", true));
                properties.put("analysisDepth", createParameter("string", "Analysis depth (basic, standard, comprehensive)", false));
                break;
            default:
                properties.put("parameters", createParameter("object", "Prompt-specific parameters", false));
        }
        
        schema.put("properties", properties);
        return schema;
    }

    private Map<String, Object> createParameter(String type, String description, boolean required) {
        Map<String, Object> param = new HashMap<>();
        param.put("type", type);
        param.put("description", description);
        return param;
    }

    private Map<String, Object> createArrayParameter(String itemType, String description, boolean required) {
        Map<String, Object> param = new HashMap<>();
        param.put("type", "array");
        param.put("description", description);
        
        Map<String, Object> items = new HashMap<>();
        items.put("type", itemType);
        param.put("items", items);
        
        return param;
    }

    private Map<String, Object> createServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "1inch-mcp-server");
        info.put("version", "1.0.0");
        info.put("description", "A comprehensive MCP server for 1inch DeFi ecosystem providing AI-enhanced access to DeFi data");
        info.put("provider", "1inch Network");
        info.put("capabilities", new String[]{"resources", "tools", "prompts"});
        return info;
    }

    // === DATA CLASSES ===

    public static class McpResourceRegistration {
        public final String uri;
        public final String description;
        public final String mimeType;
        public final String[] pathParams;
        public final String details;

        public McpResourceRegistration(String uri, String description, String mimeType, 
                                     String[] pathParams, String details) {
            this.uri = uri;
            this.description = description;
            this.mimeType = mimeType;
            this.pathParams = pathParams;
            this.details = details;
        }
    }

    public static class McpToolRegistration {
        public final String name;
        public final String description;
        public final Map<String, Object> inputSchema;
        public final String details;

        public McpToolRegistration(String name, String description, Map<String, Object> inputSchema, String details) {
            this.name = name;
            this.description = description;
            this.inputSchema = inputSchema;
            this.details = details;
        }
    }

    public static class McpPromptRegistration {
        public final String name;
        public final String description;
        public final Map<String, Object> inputSchema;
        public final String category;

        public McpPromptRegistration(String name, String description, Map<String, Object> inputSchema, String category) {
            this.name = name;
            this.description = description;
            this.inputSchema = inputSchema;
            this.category = category;
        }
    }

    public static class McpServerRegistration {
        public final List<McpResourceRegistration> resources;
        public final List<McpToolRegistration> tools;
        public final List<McpPromptRegistration> prompts;
        public final Map<String, Object> serverInfo;

        public McpServerRegistration(List<McpResourceRegistration> resources, List<McpToolRegistration> tools,
                                   List<McpPromptRegistration> prompts, Map<String, Object> serverInfo) {
            this.resources = resources;
            this.tools = tools;
            this.prompts = prompts;
            this.serverInfo = serverInfo;
        }
    }
}