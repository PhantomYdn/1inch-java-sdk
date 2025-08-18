package io.oneinch.mcp.prompts;

import io.quarkiverse.mcp.server.Prompt;
import io.quarkiverse.mcp.server.PromptArg;
import io.quarkiverse.mcp.server.PromptMessage;
import io.quarkiverse.mcp.server.TextContent;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * MCP Prompts for common DeFi analysis tasks.
 * These are user-controlled templates that provide optimized prompts for 
 * AI models to perform DeFi-specific analysis using the available tools and resources.
 */
@ApplicationScoped
public class DeFiPrompts {

    private static final Logger log = LoggerFactory.getLogger(DeFiPrompts.class);

    /**
     * Swap analysis prompt for trade optimization.
     */
    @Prompt(description = "Generate comprehensive analysis for token swaps with optimization recommendations")
    public PromptMessage swapAnalysisPrompt(
            @PromptArg(description = "Source token symbol or address") String srcToken,
            @PromptArg(description = "Destination token symbol or address") String dstToken, 
            @PromptArg(description = "Amount to swap") String amount,
            @PromptArg(description = "Chain ID (default: Ethereum)", defaultValue = "1") String chainId) {
        int chain;
        try {
            chain = chainId != null ? Integer.parseInt(chainId) : 1; // Default to Ethereum
        } catch (NumberFormatException e) {
            chain = 1; // Default to Ethereum on parse error
        }
        
        String promptText = String.format(
            "Analyze the best way to swap %s %s to %s on %s (Chain ID: %d).\n\n" +
            "Please provide a comprehensive analysis including:\n" +
            "1. **Route Analysis**: Use getSwapQuote tool to find the optimal swap route\n" +
            "2. **Price Impact**: Calculate the price impact and slippage for this trade size\n" +
            "3. **Gas Optimization**: Use analyzeGasOptimization tool to recommend optimal gas settings\n" +
            "4. **Timing**: Suggest the best timing for this swap based on network conditions\n" +
            "5. **Alternative Routes**: Compare different protocols and routes\n" +
            "6. **Risk Assessment**: Identify any risks associated with this swap\n\n" +
            "Consider factors like:\n" +
            "- Current market conditions and volatility\n" +
            "- Liquidity depth and potential slippage\n" +
            "- Gas costs and network congestion\n" +
            "- Protocol security and reliability\n\n" +
            "Provide specific, actionable recommendations with reasoning.",
            amount, srcToken, dstToken, getChainName(chain), chain
        );
        
        return PromptMessage.withUserRole(new TextContent(promptText));
    }

    /**
     * Portfolio review prompt for performance analysis.
     */
    @Prompt(description = "Generate comprehensive portfolio analysis and performance review")
    public PromptMessage portfolioReviewPrompt(
            @PromptArg(description = "Wallet address to analyze") String address,
            @PromptArg(description = "Analysis timeframe", defaultValue = "30d") String timeframe,
            @PromptArg(description = "Optional specific chain ID") String chainId) {
        String chainFilter;
        if (chainId != null && !chainId.trim().isEmpty()) {
            try {
                int chain = Integer.parseInt(chainId);
                chainFilter = String.format(" on %s (Chain ID: %d)", getChainName(chain), chain);
            } catch (NumberFormatException e) {
                chainFilter = " across all supported chains";
            }
        } else {
            chainFilter = " across all supported chains";
        }
        
        String promptText = String.format(
            "Analyze portfolio performance for address %s over %s%s.\n\n" +
            "Please provide a comprehensive portfolio review including:\n" +
            "1. **Portfolio Overview**: Use getPortfolioValue tool to get current valuation and breakdown\n" +
            "2. **Performance Metrics**: Calculate returns, P&L, and performance over the specified timeframe\n" +
            "3. **Risk Analysis**: Use analyzePortfolioRisk tool to assess risk exposure and diversification\n" +
            "4. **Yield Opportunities**: Use findYieldOpportunities tool to identify potential yield strategies\n" +
            "5. **Rebalancing**: Use generateRebalancingStrategy tool if portfolio needs optimization\n" +
            "6. **Token Analysis**: Analyze major holdings using analyzeToken tool\n\n" +
            "Focus on:\n" +
            "- Total portfolio value and allocation breakdown\n" +
            "- Risk-adjusted returns and Sharpe ratio\n" +
            "- Diversification across protocols, chains, and asset types\n" +
            "- Yield optimization opportunities\n" +
            "- Risk factors and concentration issues\n\n" +
            "Provide actionable recommendations for portfolio improvement.",
            address, timeframe, chainFilter
        );
        
        return PromptMessage.withUserRole(new TextContent(promptText));
    }

    /**
     * Market report prompt for daily DeFi market insights.
     * 
     * @param tokens Array of tokens to focus on
     * @param chains Array of chain IDs to analyze
     * @param includeGas Whether to include gas analysis
     * @return Optimized prompt for market analysis
     */
    public String marketReportPrompt(String[] tokens, Integer[] chains, Boolean includeGas) {
        String tokenList = tokens != null && tokens.length > 0 ? 
            String.join(", ", tokens) : "top DeFi tokens";
        String chainList = chains != null && chains.length > 0 ? 
            java.util.Arrays.stream(chains).map(this::getChainName).collect(java.util.stream.Collectors.joining(", ")) : 
            "major chains";
        
        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format(
            "Generate a comprehensive DeFi market report focusing on %s across %s.\n\n" +
            "Please provide analysis covering:\n" +
            "1. **Market Trends**: Use analyzeMarketTrends tool to analyze price movements and trends\n" +
            "2. **Trending Tokens**: Use getTrendingTokens tool to identify top performers\n" +
            "3. **Market Sentiment**: Use analyzeMarketSentiment tool to gauge sector sentiment\n" +
            "4. **Cross-Chain Performance**: Use compareMarketPerformanceAcrossChains for chain comparison\n" +
            "5. **Token Analysis**: Use analyzeToken tool for detailed analysis of key tokens\n",
            tokenList, chainList
        ));
        
        if (includeGas != null && includeGas) {
            prompt.append("6. **Gas Optimization**: Use analyzeGasOptimization tool for gas market analysis\n");
        }
        
        prompt.append(
            "\nKey areas to cover:\n" +
            "- Overall market direction and momentum\n" +
            "- Sector rotation and emerging trends\n" +
            "- Volatility and risk assessment\n" +
            "- Volume and liquidity analysis\n" +
            "- Cross-chain activity comparison\n"
        );
        
        if (includeGas != null && includeGas) {
            prompt.append("- Gas fee trends and optimization strategies\n");
        }
        
        prompt.append(
            "\nFormat the report with:\n" +
            "- Executive Summary with key findings\n" +
            "- Detailed analysis by sector/token\n" +
            "- Risk factors and opportunities\n" +
            "- Actionable trading/investment insights"
        );
        
        return prompt.toString();
    }

    /**
     * Yield opportunities prompt for yield farming analysis.
     * 
     * @param amount Amount available for yield farming
     * @param token Token to farm with
     * @param riskTolerance Risk tolerance (low, medium, high)
     * @param timeHorizon Investment timeframe (short, medium, long)
     * @return Optimized prompt for yield opportunity analysis
     */
    public String yieldOpportunitiesPrompt(String amount, String token, String riskTolerance, String timeHorizon) {
        return String.format(
            "Find the best yield farming opportunities for %s %s with %s risk tolerance and %s time horizon.\n\n" +
            "Please provide comprehensive yield analysis including:\n" +
            "1. **Current Opportunities**: Use findYieldOpportunities tool to discover available strategies\n" +
            "2. **Token Analysis**: Use analyzeToken tool to assess the base token's characteristics\n" +
            "3. **Protocol Comparison**: Compare different DeFi protocols for yield farming\n" +
            "4. **Risk Assessment**: Analyze risks associated with each opportunity\n" +
            "5. **Market Conditions**: Use analyzeMarketTrends tool to assess market timing\n" +
            "6. **Gas Optimization**: Use analyzeGasOptimization tool for cost-effective execution\n\n" +
            "Consider these factors:\n" +
            "- APY/APR rates and sustainability\n" +
            "- Impermanent loss potential\n" +
            "- Protocol security and audit status\n" +
            "- Liquidity depth and exit strategy\n" +
            "- Token emission schedules and dilution\n" +
            "- Correlation risks and diversification\n\n" +
            "Risk tolerance: %s means:\n" +
            "%s\n\n" +
            "Time horizon: %s means:\n" +
            "%s\n\n" +
            "Provide ranked recommendations with risk-adjusted returns and implementation steps.",
            amount, token, riskTolerance, timeHorizon,
            riskTolerance,
            getRiskToleranceDescription(riskTolerance),
            timeHorizon,
            getTimeHorizonDescription(timeHorizon)
        );
    }

    /**
     * Risk assessment prompt for portfolio risk analysis.
     * 
     * @param address Portfolio address to analyze
     * @param stressTestScenarios Scenarios to test against
     * @param chainId Optional specific chain
     * @return Optimized prompt for risk assessment
     */
    public String riskAssessmentPrompt(String address, String[] stressTestScenarios, Integer chainId) {
        String scenarios = stressTestScenarios != null && stressTestScenarios.length > 0 ?
            String.join(", ", stressTestScenarios) : "market crash, liquidity crisis, protocol hack";
        String chainFilter = chainId != null ?
            String.format(" on %s (Chain ID: %d)", getChainName(chainId), chainId) : "";
        
        return String.format(
            "Conduct a comprehensive risk assessment for portfolio at address %s%s.\n\n" +
            "Please provide detailed risk analysis including:\n" +
            "1. **Portfolio Overview**: Use getPortfolioValue tool to understand current positions\n" +
            "2. **Risk Metrics**: Use analyzePortfolioRisk tool for quantitative risk assessment\n" +
            "3. **Token Risk Analysis**: Use analyzeToken tool for individual asset risk evaluation\n" +
            "4. **Concentration Risk**: Analyze position sizes and diversification\n" +
            "5. **Protocol Risk**: Assess smart contract and protocol-specific risks\n" +
            "6. **Market Risk**: Use analyzeMarketTrends tool to assess market conditions\n" +
            "7. **Liquidity Risk**: Evaluate position liquidity and exit scenarios\n\n" +
            "Stress test the portfolio against these scenarios:\n" +
            "%s\n\n" +
            "Risk analysis should cover:\n" +
            "- Value at Risk (VaR) calculations\n" +
            "- Maximum drawdown potential\n" +
            "- Correlation analysis between positions\n" +
            "- Smart contract risk exposure\n" +
            "- Liquidation risks and margin safety\n" +
            "- Tail risk scenarios and black swan events\n\n" +
            "Provide:\n" +
            "- Risk score with justification\n" +
            "- Specific risk mitigation strategies\n" +
            "- Portfolio rebalancing recommendations\n" +
            "- Hedging strategies if applicable",
            address, chainFilter, scenarios
        );
    }

    /**
     * Price comparison prompt for cross-chain price analysis.
     * 
     * @param token Token symbol or address to compare
     * @param chains Array of chain IDs to compare across
     * @param includeArbitrage Whether to analyze arbitrage opportunities
     * @return Optimized prompt for price comparison
     */
    public String priceComparisonPrompt(String token, Integer[] chains, Boolean includeArbitrage) {
        String chainList = chains != null && chains.length > 0 ?
            java.util.Arrays.stream(chains).map(this::getChainName).collect(java.util.stream.Collectors.joining(", ")) :
            "major chains (Ethereum, Polygon, BSC, Arbitrum, Optimism)";
        
        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format(
            "Compare %s prices across %s and provide detailed analysis.\n\n" +
            "Please provide comprehensive price analysis including:\n" +
            "1. **Price Discovery**: Use searchTokens tool to find the token on each chain\n" +
            "2. **Price Comparison**: Use compareTokenPricesAcrossChains tool for cross-chain analysis\n" +
            "3. **Token Analysis**: Use analyzeToken tool to verify token authenticity on each chain\n" +
            "4. **Market Trends**: Use analyzeMarketTrends tool to understand price movements\n" +
            "5. **Liquidity Analysis**: Assess trading volume and liquidity depth\n",
            token, chainList
        ));
        
        if (includeArbitrage != null && includeArbitrage) {
            prompt.append(
                "6. **Arbitrage Analysis**: Identify profitable arbitrage opportunities\n" +
                "7. **Gas Optimization**: Use analyzeGasOptimization tool for arbitrage cost analysis\n"
            );
        }
        
        prompt.append(
            "\nAnalysis should include:\n" +
            "- Price differences between chains (absolute and percentage)\n" +
            "- Trading volume and liquidity comparison\n" +
            "- Historical price correlation analysis\n" +
            "- Market maker presence and spread analysis\n"
        );
        
        if (includeArbitrage != null && includeArbitrage) {
            prompt.append(
                "- Arbitrage opportunity identification\n" +
                "- Transaction costs and profitability analysis\n" +
                "- Risk factors for arbitrage execution\n"
            );
        }
        
        prompt.append(
            "\nProvide:\n" +
            "- Best price execution recommendations\n" +
            "- Risk factors for each chain\n" +
            "- Optimal trading strategies\n" +
            "- Market efficiency insights"
        );
        
        return prompt.toString();
    }

    /**
     * Token research prompt for comprehensive token analysis.
     * 
     * @param token Token symbol or address
     * @param chainId Chain ID where token resides
     * @param analysisDepth Depth of analysis (basic, standard, comprehensive)
     * @return Optimized prompt for token research
     */
    public String tokenResearchPrompt(String token, Integer chainId, String analysisDepth) {
        String depth = analysisDepth != null ? analysisDepth : "standard";
        
        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format(
            "Conduct %s research analysis of %s on %s (Chain ID: %d).\n\n" +
            "Please provide comprehensive token research including:\n" +
            "1. **Basic Analysis**: Use analyzeToken tool for fundamental token data\n" +
            "2. **Market Analysis**: Use analyzeMarketTrends tool for price and volume trends\n" +
            "3. **Cross-Chain Presence**: Use searchTokens tool to find token on other chains\n",
            depth, token, getChainName(chainId), chainId
        ));
        
        if ("comprehensive".equals(depth)) {
            prompt.append(
                "4. **Risk Assessment**: Comprehensive risk evaluation\n" +
                "5. **Yield Opportunities**: Identify farming and staking options\n" +
                "6. **Protocol Integration**: Analyze DeFi protocol usage\n"
            );
        } else if ("standard".equals(depth)) {
            prompt.append("4. **Risk Factors**: Key risk identification\n");
        }
        
        prompt.append(
            "\nResearch areas to cover:\n" +
            "- Token fundamentals (supply, distribution, utility)\n" +
            "- Price performance and volatility analysis\n" +
            "- Trading volume and liquidity metrics\n" +
            "- Market capitalization and ranking\n"
        );
        
        if (!"basic".equals(depth)) {
            prompt.append(
                "- Use cases and adoption metrics\n" +
                "- Competition and market positioning\n" +
                "- Technical and fundamental analysis\n"
            );
        }
        
        if ("comprehensive".equals(depth)) {
            prompt.append(
                "- Tokenomics deep dive\n" +
                "- Governance mechanisms\n" +
                "- Development activity\n" +
                "- Partnership ecosystem\n" +
                "- Regulatory considerations\n"
            );
        }
        
        prompt.append(
            "\nProvide:\n" +
            "- Investment thesis (bullish/bearish/neutral)\n" +
            "- Price targets and timeframes\n" +
            "- Risk-reward assessment\n" +
            "- Trading/investment recommendations"
        );
        
        return prompt.toString();
    }

    /**
     * Get all available prompts with their descriptions.
     * 
     * @return Map of prompt names to descriptions
     */
    public Map<String, String> getAvailablePrompts() {
        Map<String, String> prompts = new HashMap<>();
        
        prompts.put("swap-analysis", 
            "Analyze optimal swap routes, pricing, and execution strategies for token trades");
        prompts.put("portfolio-review", 
            "Comprehensive portfolio analysis including performance, risk, and optimization");
        prompts.put("market-report", 
            "Daily DeFi market insights covering trends, sentiment, and opportunities");
        prompts.put("yield-opportunities", 
            "Discover and analyze yield farming strategies and opportunities");
        prompts.put("risk-assessment", 
            "Comprehensive portfolio and position risk analysis with stress testing");
        prompts.put("price-comparison", 
            "Cross-chain price analysis with arbitrage opportunity identification");
        prompts.put("token-research", 
            "In-depth token research and investment analysis");
        
        return prompts;
    }

    /**
     * Get prompt template by name with parameters.
     * 
     * @param promptName Name of the prompt template
     * @param parameters Parameters for the prompt
     * @return Formatted prompt template
     */
    public PromptMessage getPromptTemplate(String promptName, Map<String, Object> parameters) {
        switch (promptName.toLowerCase()) {
            case "swap-analysis":
                return swapAnalysisPrompt(
                    (String) parameters.get("srcToken"),
                    (String) parameters.get("dstToken"), 
                    (String) parameters.get("amount"),
                    parameters.get("chainId") != null ? parameters.get("chainId").toString() : "1"
                );
            case "portfolio-review":
                return portfolioReviewPrompt(
                    (String) parameters.get("address"),
                    (String) parameters.get("timeframe"),
                    parameters.get("chainId") != null ? parameters.get("chainId").toString() : null
                );
            case "market-report":
                return PromptMessage.withUserRole(new TextContent("Market report functionality coming soon"));
            case "yield-opportunities":
                return PromptMessage.withUserRole(new TextContent("Yield opportunities analysis functionality coming soon"));
            case "risk-assessment":
                return PromptMessage.withUserRole(new TextContent("Risk assessment functionality coming soon"));
            case "price-comparison":
                return PromptMessage.withUserRole(new TextContent("Price comparison functionality coming soon"));
            case "token-research":
                return PromptMessage.withUserRole(new TextContent("Token research functionality coming soon"));
            default:
                return PromptMessage.withUserRole(new TextContent(
                    String.format("Unknown prompt template: %s. Available prompts: %s", 
                        promptName, String.join(", ", getAvailablePrompts().keySet()))));
        }
    }

    // === UTILITY METHODS ===

    private String getChainName(Integer chainId) {
        switch (chainId) {
            case 1:
                return "Ethereum";
            case 56:
                return "BNB Smart Chain";
            case 137:
                return "Polygon";
            case 42161:
                return "Arbitrum One";
            case 10:
                return "Optimism";
            case 43114:
                return "Avalanche C-Chain";
            case 100:
                return "Gnosis Chain";
            case 250:
                return "Fantom";
            case 1313161554:
                return "Aurora";
            case 1284:
                return "Moonbeam";
            case 25:
                return "Cronos";
            case 42220:
                return "Celo";
            case 8217:
                return "Klaytn";
            default:
                return "Chain " + chainId;
        }
    }

    private String getRiskToleranceDescription(String riskTolerance) {
        switch (riskTolerance.toLowerCase()) {
            case "low":
                return "- Focus on established protocols with strong security track records\n" +
                       "- Prefer stable assets and conservative strategies\n" +
                       "- Accept lower returns for reduced risk\n" +
                       "- Avoid experimental or unaudited protocols";
            case "medium":
                return "- Balance between risk and reward\n" +
                       "- Consider both established and emerging protocols\n" +
                       "- Accept moderate risk for better returns\n" +
                       "- Diversify across risk levels";
            case "high":
                return "- Willing to take significant risks for high returns\n" +
                       "- Consider experimental and high-yield protocols\n" +
                       "- Accept potential for significant losses\n" +
                       "- Focus on maximum yield opportunities";
            default:
                return "Standard risk considerations apply";
        }
    }

    private String getTimeHorizonDescription(String timeHorizon) {
        switch (timeHorizon.toLowerCase()) {
            case "short":
                return "- Focus on opportunities with quick returns (days to weeks)\n" +
                       "- Consider liquidity and exit flexibility\n" +
                       "- Accept lower yields for shorter commitment\n" +
                       "- Monitor actively for optimal timing";
            case "medium":
                return "- Focus on opportunities with medium-term returns (weeks to months)\n" +
                       "- Balance yield optimization with flexibility\n" +
                       "- Consider seasonal patterns and cycles\n" +
                       "- Moderate monitoring required";
            case "long":
                return "- Focus on opportunities with long-term returns (months to years)\n" +
                       "- Prioritize sustainable yield and compounding\n" +
                       "- Accept lock-up periods for better rates\n" +
                       "- Set-and-forget strategies preferred";
            default:
                return "Standard time horizon considerations apply";
        }
    }
}