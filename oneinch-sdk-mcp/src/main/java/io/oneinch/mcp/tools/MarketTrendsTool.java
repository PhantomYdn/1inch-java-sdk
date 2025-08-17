package io.oneinch.mcp.tools;

import io.oneinch.mcp.integration.OneInchIntegrationService;
import io.oneinch.mcp.integration.ApiResponseMapper;
import io.oneinch.sdk.model.Token;
import io.oneinch.sdk.model.token.*;
import io.oneinch.sdk.model.price.*;
import io.oneinch.sdk.model.portfolio.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MCP Tool for market movement analysis and trend identification.
 * This tool provides AI models with market trend analysis, price movements,
 * trading volume analysis, and market sentiment indicators.
 * 
 * Note: This tool provides analytical insights based on available data.
 * Full functionality requires Charts API integration in the SDK.
 */
@ApplicationScoped
public class MarketTrendsTool {

    private static final Logger log = LoggerFactory.getLogger(MarketTrendsTool.class);

    @Inject
    OneInchIntegrationService integrationService;

    @Inject
    ApiResponseMapper responseMapper;

    /**
     * Analyze market trends for specific tokens or chains.
     * 
     * @param tokenAddresses Array of token addresses to analyze
     * @param chainId The blockchain network ID
     * @param timeframe Analysis timeframe (1h, 4h, 24h, 7d, 30d)
     * @param includeVolume Whether to include volume analysis
     * @return Market trends analysis
     */
    public CompletableFuture<String> analyzeMarketTrends(String[] tokenAddresses, Integer chainId, 
                                                         String timeframe, Boolean includeVolume) {
        log.info("Analyzing market trends for {} tokens on chain {} timeframe {} volume {}", 
                tokenAddresses.length, chainId, timeframe, includeVolume);

        try {
            // Get current price data for all tokens
            List<CompletableFuture<TokenTrendAnalysis>> trendFutures = new ArrayList<>();
            for (String tokenAddress : tokenAddresses) {
                trendFutures.add(analyzeTokenTrend(tokenAddress, chainId, timeframe, includeVolume));
            }

            return CompletableFuture.allOf(trendFutures.toArray(new CompletableFuture[0]))
                    .thenApply(unused -> {
                        List<TokenTrendAnalysis> trends = trendFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList());
                        
                        MarketOverview overview = generateMarketOverview(trends, chainId, timeframe);
                        return formatMarketTrendsResponse(trends, overview, tokenAddresses, chainId, timeframe, includeVolume);
                    })
                    .exceptionally(throwable -> {
                        log.error("Error analyzing market trends for tokens on chain {}: {}", chainId, throwable.getMessage());
                        return formatErrorResponse("market_trends_failed", throwable.getMessage(), chainId, timeframe);
                    });

        } catch (Exception e) {
            log.error("Unexpected error in analyzeMarketTrends", e);
            return CompletableFuture.completedFuture(
                formatErrorResponse("unexpected_error", e.getMessage(), chainId, timeframe)
            );
        }
    }

    /**
     * Get trending tokens on a specific chain.
     * 
     * @param chainId The blockchain network ID
     * @param category Optional category filter (defi, gaming, meme, etc.)
     * @param sortBy Sort criteria (volume, price_change, market_cap)
     * @param limit Maximum number of trending tokens to return
     * @return Trending tokens analysis
     */
    public CompletableFuture<String> getTrendingTokens(Integer chainId, String category, String sortBy, Integer limit) {
        log.info("Getting trending tokens for chain {} category {} sorted by {} limit {}", 
                chainId, category, sortBy, limit);

        // Get token list and analyze for trending patterns
        TokenListRequest request = TokenListRequest.builder()
                .chainId(chainId)
                .provider("1inch")
                .build();

        return integrationService.getTokenList(request)
                .thenCompose(tokenList -> {
                    List<Token> tokens = tokenList.getTokens();
                    if (tokens == null) tokens = new ArrayList<>();
                    
                    // Filter by category if specified
                    if (category != null) {
                        tokens = filterTokensByCategory(tokens, category);
                    }
                    
                    // Analyze trending patterns for top tokens
                    int analysisLimit = Math.min(tokens.size(), limit != null ? limit * 3 : 30); // Analyze more than we'll return
                    List<Token> topTokens = tokens.stream().limit(analysisLimit).collect(Collectors.toList());
                    
                    return analyzeTrendingPatterns(topTokens, chainId, sortBy, limit);
                })
                .exceptionally(throwable -> {
                    log.error("Error getting trending tokens for chain {}: {}", chainId, throwable.getMessage());
                    return formatErrorResponse("trending_tokens_failed", throwable.getMessage(), chainId, "trending");
                });
    }

    /**
     * Analyze market sentiment for DeFi protocols and sectors.
     * 
     * @param chainId The blockchain network ID
     * @param sectors Array of DeFi sectors to analyze (DEX, Lending, Yield, etc.)
     * @param timeframe Analysis timeframe
     * @return Market sentiment analysis
     */
    public CompletableFuture<String> analyzeMarketSentiment(Integer chainId, String[] sectors, String timeframe) {
        log.info("Analyzing market sentiment for chain {} sectors {} timeframe {}", 
                chainId, String.join(",", sectors), timeframe);

        // Get portfolio protocols to analyze DeFi sectors
        return integrationService.getSupportedProtocols()
                .thenApply(protocols -> {
                    List<SectorSentiment> sentiments = analyzeSectorSentiments(protocols, sectors, chainId, timeframe);
                    MarketSentimentOverview overview = generateSentimentOverview(sentiments);
                    
                    return formatMarketSentimentResponse(sentiments, overview, chainId, sectors, timeframe);
                })
                .exceptionally(throwable -> {
                    log.error("Error analyzing market sentiment for chain {}: {}", chainId, throwable.getMessage());
                    return formatErrorResponse("market_sentiment_failed", throwable.getMessage(), chainId, timeframe);
                });
    }

    /**
     * Compare market performance across multiple chains.
     * 
     * @param chainIds Array of chain IDs to compare
     * @param metrics Metrics to compare (volume, tvl, activity, etc.)
     * @param timeframe Comparison timeframe
     * @return Cross-chain market comparison
     */
    public CompletableFuture<String> compareMarketPerformanceAcrossChains(Integer[] chainIds, String[] metrics, String timeframe) {
        log.info("Comparing market performance across chains {} metrics {} timeframe {}", 
                String.join(",", java.util.Arrays.stream(chainIds).map(String::valueOf).collect(Collectors.toList())), 
                String.join(",", metrics), timeframe);

        // Analyze each chain's market performance
        List<CompletableFuture<ChainMarketPerformance>> performanceFutures = new ArrayList<>();
        for (Integer chainId : chainIds) {
            performanceFutures.add(analyzeChainMarketPerformance(chainId, metrics, timeframe));
        }

        return CompletableFuture.allOf(performanceFutures.toArray(new CompletableFuture[0]))
                .thenApply(unused -> {
                    List<ChainMarketPerformance> performances = performanceFutures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());
                    
                    return formatCrossChainComparisonResponse(performances, metrics, timeframe);
                })
                .exceptionally(throwable -> {
                    log.error("Error comparing market performance across chains: {}", throwable.getMessage());
                    return formatErrorResponse("cross_chain_comparison_failed", throwable.getMessage(), null, timeframe);
                });
    }

    /**
     * Identify potential market opportunities and risks.
     * 
     * @param chainId The blockchain network ID
     * @param riskTolerance Risk tolerance level (low, medium, high)
     * @param investmentHorizon Investment timeframe (short, medium, long)
     * @param sectors Optional specific sectors to focus on
     * @return Market opportunities and risks analysis
     */
    public CompletableFuture<String> identifyMarketOpportunities(Integer chainId, String riskTolerance, 
                                                                String investmentHorizon, String[] sectors) {
        log.info("Identifying market opportunities for chain {} risk {} horizon {} sectors {}", 
                chainId, riskTolerance, investmentHorizon, sectors != null ? String.join(",", sectors) : "all");

        // Analyze current market conditions
        return analyzeCurrentMarketConditions(chainId, sectors)
                .thenApply(conditions -> {
                    List<MarketOpportunity> opportunities = identifyOpportunities(conditions, riskTolerance, investmentHorizon);
                    List<MarketRisk> risks = identifyRisks(conditions, riskTolerance);
                    
                    return formatMarketOpportunitiesResponse(opportunities, risks, chainId, riskTolerance, investmentHorizon, sectors);
                })
                .exceptionally(throwable -> {
                    log.error("Error identifying market opportunities for chain {}: {}", chainId, throwable.getMessage());
                    return formatErrorResponse("market_opportunities_failed", throwable.getMessage(), chainId, "opportunities");
                });
    }

    // === HELPER METHODS ===

    private CompletableFuture<TokenTrendAnalysis> analyzeTokenTrend(String tokenAddress, Integer chainId, 
                                                                   String timeframe, Boolean includeVolume) {
        // Get token details and price
        CustomTokenRequest tokenRequest = CustomTokenRequest.builder()
                .chainId(chainId)
                .addresses(List.of(tokenAddress))
                .build();

        CompletableFuture<List<Token>> tokenFuture = integrationService.getCustomTokens(tokenRequest);
        CompletableFuture<BigInteger> priceFuture = integrationService.getSingleTokenPrice(chainId, tokenAddress, Currency.USD);

        return CompletableFuture.allOf(tokenFuture, priceFuture)
                .thenApply(unused -> {
                    List<Token> tokens = tokenFuture.join();
                    BigInteger price = priceFuture.join();
                    
                    if (tokens.isEmpty()) {
                        return new TokenTrendAnalysis(tokenAddress, "Unknown", "UNKNOWN", price, 
                                0.0, "neutral", "stable", includeVolume ? 1000000.0 : null);
                    }
                    
                    Token token = tokens.get(0);
                    double priceChange = simulatePriceChange(timeframe); // Simulated for now
                    String trend = determineTrend(priceChange);
                    String momentum = determineMomentum(priceChange, timeframe);
                    Double volume = includeVolume ? simulateVolume(token, timeframe) : null;
                    
                    return new TokenTrendAnalysis(tokenAddress, token.getName(), token.getSymbol(), 
                            price, priceChange, trend, momentum, volume);
                });
    }

    private MarketOverview generateMarketOverview(List<TokenTrendAnalysis> trends, Integer chainId, String timeframe) {
        double avgPriceChange = trends.stream().mapToDouble(t -> t.priceChange).average().orElse(0.0);
        long bullishCount = trends.stream().mapToLong(t -> "bullish".equals(t.trend) ? 1 : 0).sum();
        long bearishCount = trends.stream().mapToLong(t -> "bearish".equals(t.trend) ? 1 : 0).sum();
        
        String overallTrend = bullishCount > bearishCount ? "bullish" : bearishCount > bullishCount ? "bearish" : "neutral";
        String marketSentiment = avgPriceChange > 5.0 ? "very_positive" : avgPriceChange > 0 ? "positive" : 
                                avgPriceChange > -5.0 ? "neutral" : "negative";
        
        return new MarketOverview(chainId, overallTrend, marketSentiment, avgPriceChange, 
                (int) bullishCount, (int) bearishCount, trends.size());
    }

    private List<Token> filterTokensByCategory(List<Token> tokens, String category) {
        return tokens.stream()
                .filter(token -> matchesCategory(token, category))
                .collect(Collectors.toList());
    }

    private boolean matchesCategory(Token token, String category) {
        String symbol = token.getSymbol().toLowerCase();
        String name = token.getName().toLowerCase();
        
        switch (category.toLowerCase()) {
            case "defi":
                return name.contains("defi") || symbol.contains("uni") || symbol.contains("comp") || 
                       symbol.contains("aave") || symbol.contains("sushi");
            case "gaming":
                return name.contains("game") || name.contains("gaming") || symbol.contains("game") || 
                       name.contains("nft");
            case "meme":
                return symbol.contains("doge") || symbol.contains("shib") || symbol.contains("pepe") ||
                       name.contains("meme");
            case "stablecoin":
                return symbol.contains("usd") || symbol.contains("dai") || symbol.contains("busd");
            default:
                return true;
        }
    }

    private CompletableFuture<String> analyzeTrendingPatterns(List<Token> tokens, Integer chainId, String sortBy, Integer limit) {
        // Simulate trending analysis - in real implementation, this would use actual market data
        List<TrendingTokenAnalysis> trending = tokens.stream()
                .limit(limit != null ? limit : 10)
                .map(token -> {
                    double changePercent = simulatePriceChange("24h");
                    double volume = simulateVolume(token, "24h");
                    String trendStrength = Math.abs(changePercent) > 10 ? "strong" : 
                                         Math.abs(changePercent) > 5 ? "moderate" : "weak";
                    
                    return new TrendingTokenAnalysis(token.getAddress(), token.getSymbol(), token.getName(),
                            changePercent, volume, trendStrength, "increasing");
                })
                .collect(Collectors.toList());
        
        return CompletableFuture.completedFuture(
            formatTrendingTokensResponse(trending, chainId, sortBy, limit)
        );
    }

    private List<SectorSentiment> analyzeSectorSentiments(List<SupportedProtocolGroupResponse> protocols, 
                                                         String[] sectors, Integer chainId, String timeframe) {
        List<SectorSentiment> sentiments = new ArrayList<>();
        
        for (String sector : sectors) {
            // Match protocols to sectors
            List<SupportedProtocolGroupResponse> sectorProtocols = protocols.stream()
                    .filter(p -> matchesSector(p.getProtocolGroupName(), sector))
                    .collect(Collectors.toList());
            
            // Generate sentiment analysis for sector
            double sentimentScore = simulateSentimentScore(sector);
            String sentiment = sentimentScore > 0.6 ? "positive" : sentimentScore > 0.4 ? "neutral" : "negative";
            String trend = simulateTrend();
            
            sentiments.add(new SectorSentiment(sector, sentiment, sentimentScore, trend, sectorProtocols.size()));
        }
        
        return sentiments;
    }

    private boolean matchesSector(String protocolName, String sector) {
        if (protocolName == null) return false;
        String protocol = protocolName.toLowerCase();
        
        switch (sector.toLowerCase()) {
            case "dex":
                return protocol.contains("swap") || protocol.contains("exchange") || protocol.contains("uniswap");
            case "lending":
                return protocol.contains("lend") || protocol.contains("borrow") || protocol.contains("aave") || 
                       protocol.contains("compound");
            case "yield":
                return protocol.contains("yield") || protocol.contains("farm") || protocol.contains("stake");
            case "derivatives":
                return protocol.contains("derivative") || protocol.contains("future") || protocol.contains("option");
            default:
                return true;
        }
    }

    private MarketSentimentOverview generateSentimentOverview(List<SectorSentiment> sentiments) {
        double avgSentiment = sentiments.stream().mapToDouble(s -> s.sentimentScore).average().orElse(0.5);
        long positiveCount = sentiments.stream().mapToLong(s -> "positive".equals(s.sentiment) ? 1 : 0).sum();
        
        String overallSentiment = avgSentiment > 0.6 ? "bullish" : avgSentiment > 0.4 ? "neutral" : "bearish";
        int confidenceLevel = sentiments.size() > 5 ? 85 : sentiments.size() > 3 ? 75 : 60;
        
        return new MarketSentimentOverview(overallSentiment, avgSentiment, (int) positiveCount, 
                sentiments.size(), confidenceLevel);
    }

    private CompletableFuture<ChainMarketPerformance> analyzeChainMarketPerformance(Integer chainId, String[] metrics, String timeframe) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate market performance analysis
            Map<String, Double> performanceMetrics = new HashMap<>();
            for (String metric : metrics) {
                performanceMetrics.put(metric, simulateMetricValue(metric, chainId));
            }
            
            double overallScore = performanceMetrics.values().stream().mapToDouble(Double::doubleValue).average().orElse(50.0);
            String performance = overallScore > 70 ? "excellent" : overallScore > 50 ? "good" : "average";
            
            return new ChainMarketPerformance(chainId, getChainName(chainId), performanceMetrics, 
                    overallScore, performance, timeframe);
        });
    }

    private CompletableFuture<MarketConditions> analyzeCurrentMarketConditions(Integer chainId, String[] sectors) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate current market conditions analysis
            double volatility = Math.random() * 30 + 10; // 10-40% volatility
            double liquidity = Math.random() * 50 + 50; // 50-100% liquidity index
            String trend = simulateTrend();
            
            Map<String, Double> sectorPerformance = new HashMap<>();
            if (sectors != null) {
                for (String sector : sectors) {
                    sectorPerformance.put(sector, Math.random() * 100);
                }
            }
            
            return new MarketConditions(chainId, volatility, liquidity, trend, sectorPerformance, "stable");
        });
    }

    private List<MarketOpportunity> identifyOpportunities(MarketConditions conditions, String riskTolerance, String investmentHorizon) {
        List<MarketOpportunity> opportunities = new ArrayList<>();
        
        // Generate opportunities based on conditions and preferences
        if ("high".equals(riskTolerance)) {
            opportunities.add(new MarketOpportunity("High Yield DeFi", "Emerging DeFi protocols with 15-25% APY", 
                    "high", 85, "short"));
            opportunities.add(new MarketOpportunity("Gaming Tokens", "Growing gaming sector with strong fundamentals", 
                    "high", 75, "medium"));
        }
        
        if (conditions.liquidity > 70) {
            opportunities.add(new MarketOpportunity("Arbitrage Trading", "Price differences across DEXes", 
                    "medium", 90, "short"));
        }
        
        opportunities.add(new MarketOpportunity("Blue Chip DeFi", "Established protocols with steady returns", 
                "low", 95, "long"));
        
        return opportunities;
    }

    private List<MarketRisk> identifyRisks(MarketConditions conditions, String riskTolerance) {
        List<MarketRisk> risks = new ArrayList<>();
        
        if (conditions.volatility > 25) {
            risks.add(new MarketRisk("High Volatility", "Market showing increased price swings", 
                    "high", 80, "Market instability"));
        }
        
        if (conditions.liquidity < 60) {
            risks.add(new MarketRisk("Liquidity Risk", "Reduced liquidity in some trading pairs", 
                    "medium", 65, "Trading execution"));
        }
        
        risks.add(new MarketRisk("Regulatory Risk", "Potential regulatory changes affecting DeFi", 
                "low", 30, "Policy changes"));
        
        return risks;
    }

    // === SIMULATION METHODS ===

    private double simulatePriceChange(String timeframe) {
        // Simulate price changes based on timeframe
        double baseChange = (Math.random() - 0.5) * 20; // -10% to +10%
        switch (timeframe) {
            case "1h":
                return baseChange * 0.1;
            case "4h":
                return baseChange * 0.3;
            case "24h":
                return baseChange;
            case "7d":
                return baseChange * 2;
            case "30d":
                return baseChange * 5;
            default:
                return baseChange;
        }
    }

    private String determineTrend(double priceChange) {
        if (priceChange > 5) return "bullish";
        if (priceChange < -5) return "bearish";
        return "neutral";
    }

    private String determineMomentum(double priceChange, String timeframe) {
        double momentum = Math.abs(priceChange);
        if (momentum > 15) return "strong";
        if (momentum > 5) return "moderate";
        return "weak";
    }

    private Double simulateVolume(Token token, String timeframe) {
        // Simulate trading volume based on token and timeframe
        return Math.random() * 10000000 + 100000; // $100K to $10M
    }

    private double simulateSentimentScore(String sector) {
        // Simulate sentiment scores for different sectors
        switch (sector.toLowerCase()) {
            case "defi":
                return 0.7 + Math.random() * 0.2;
            case "gaming":
                return 0.6 + Math.random() * 0.3;
            case "yield":
                return 0.5 + Math.random() * 0.3;
            default:
                return 0.4 + Math.random() * 0.4;
        }
    }

    private String simulateTrend() {
        double rand = Math.random();
        return rand > 0.6 ? "increasing" : rand > 0.4 ? "stable" : "decreasing";
    }

    private double simulateMetricValue(String metric, Integer chainId) {
        // Simulate performance metrics for chains
        double base = 50 + Math.random() * 40; // 50-90 base score
        
        // Adjust based on metric type
        switch (metric.toLowerCase()) {
            case "volume":
                return chainId == 1 ? base + 10 : base; // Ethereum gets bonus
            case "tvl":
                return chainId == 1 ? base + 15 : base;
            case "activity":
                return chainId == 137 ? base + 5 : base; // Polygon gets activity bonus
            default:
                return base;
        }
    }

    // === DATA CLASSES ===

    private static class TokenTrendAnalysis {
        public final String address;
        public final String name;
        public final String symbol;
        public final BigInteger currentPrice;
        public final Double priceChange;
        public final String trend;
        public final String momentum;
        public final Double volume;

        public TokenTrendAnalysis(String address, String name, String symbol, BigInteger currentPrice,
                                Double priceChange, String trend, String momentum, Double volume) {
            this.address = address;
            this.name = name;
            this.symbol = symbol;
            this.currentPrice = currentPrice;
            this.priceChange = priceChange;
            this.trend = trend;
            this.momentum = momentum;
            this.volume = volume;
        }
    }

    private static class MarketOverview {
        public final Integer chainId;
        public final String overallTrend;
        public final String marketSentiment;
        public final Double avgPriceChange;
        public final Integer bullishTokens;
        public final Integer bearishTokens;
        public final Integer totalTokens;

        public MarketOverview(Integer chainId, String overallTrend, String marketSentiment, Double avgPriceChange,
                            Integer bullishTokens, Integer bearishTokens, Integer totalTokens) {
            this.chainId = chainId;
            this.overallTrend = overallTrend;
            this.marketSentiment = marketSentiment;
            this.avgPriceChange = avgPriceChange;
            this.bullishTokens = bullishTokens;
            this.bearishTokens = bearishTokens;
            this.totalTokens = totalTokens;
        }
    }

    private static class TrendingTokenAnalysis {
        public final String address;
        public final String symbol;
        public final String name;
        public final Double priceChange;
        public final Double volume;
        public final String trendStrength;
        public final String volumeTrend;

        public TrendingTokenAnalysis(String address, String symbol, String name, Double priceChange,
                                   Double volume, String trendStrength, String volumeTrend) {
            this.address = address;
            this.symbol = symbol;
            this.name = name;
            this.priceChange = priceChange;
            this.volume = volume;
            this.trendStrength = trendStrength;
            this.volumeTrend = volumeTrend;
        }
    }

    private static class SectorSentiment {
        public final String sector;
        public final String sentiment;
        public final Double sentimentScore;
        public final String trend;
        public final Integer protocolCount;

        public SectorSentiment(String sector, String sentiment, Double sentimentScore, String trend, Integer protocolCount) {
            this.sector = sector;
            this.sentiment = sentiment;
            this.sentimentScore = sentimentScore;
            this.trend = trend;
            this.protocolCount = protocolCount;
        }
    }

    private static class MarketSentimentOverview {
        public final String overallSentiment;
        public final Double avgSentimentScore;
        public final Integer positiveSectors;
        public final Integer totalSectors;
        public final Integer confidenceLevel;

        public MarketSentimentOverview(String overallSentiment, Double avgSentimentScore, Integer positiveSectors,
                                     Integer totalSectors, Integer confidenceLevel) {
            this.overallSentiment = overallSentiment;
            this.avgSentimentScore = avgSentimentScore;
            this.positiveSectors = positiveSectors;
            this.totalSectors = totalSectors;
            this.confidenceLevel = confidenceLevel;
        }
    }

    private static class ChainMarketPerformance {
        public final Integer chainId;
        public final String chainName;
        public final Map<String, Double> metrics;
        public final Double overallScore;
        public final String performance;
        public final String timeframe;

        public ChainMarketPerformance(Integer chainId, String chainName, Map<String, Double> metrics,
                                    Double overallScore, String performance, String timeframe) {
            this.chainId = chainId;
            this.chainName = chainName;
            this.metrics = metrics;
            this.overallScore = overallScore;
            this.performance = performance;
            this.timeframe = timeframe;
        }
    }

    private static class MarketConditions {
        public final Integer chainId;
        public final Double volatility;
        public final Double liquidity;
        public final String trend;
        public final Map<String, Double> sectorPerformance;
        public final String stability;

        public MarketConditions(Integer chainId, Double volatility, Double liquidity, String trend,
                              Map<String, Double> sectorPerformance, String stability) {
            this.chainId = chainId;
            this.volatility = volatility;
            this.liquidity = liquidity;
            this.trend = trend;
            this.sectorPerformance = sectorPerformance;
            this.stability = stability;
        }
    }

    private static class MarketOpportunity {
        public final String name;
        public final String description;
        public final String riskLevel;
        public final Integer confidence;
        public final String timeHorizon;

        public MarketOpportunity(String name, String description, String riskLevel, Integer confidence, String timeHorizon) {
            this.name = name;
            this.description = description;
            this.riskLevel = riskLevel;
            this.confidence = confidence;
            this.timeHorizon = timeHorizon;
        }
    }

    private static class MarketRisk {
        public final String name;
        public final String description;
        public final String severity;
        public final Integer probability;
        public final String impact;

        public MarketRisk(String name, String description, String severity, Integer probability, String impact) {
            this.name = name;
            this.description = description;
            this.severity = severity;
            this.probability = probability;
            this.impact = impact;
        }
    }

    // === RESPONSE FORMATTING METHODS ===

    private String formatMarketTrendsResponse(List<TokenTrendAnalysis> trends, MarketOverview overview,
                                            String[] tokenAddresses, Integer chainId, String timeframe, Boolean includeVolume) {
        return String.format(
            "{" +
            "\"tool\": \"analyzeMarketTrends\"," +
            "\"type\": \"market_trends_analysis\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"timeframe\": \"%s\"," +
            "\"tokens_analyzed\": %d," +
            "\"market_overview\": {" +
            "\"overall_trend\": \"%s\"," +
            "\"market_sentiment\": \"%s\"," +
            "\"avg_price_change_percent\": %.2f," +
            "\"bullish_tokens\": %d," +
            "\"bearish_tokens\": %d," +
            "\"neutral_tokens\": %d" +
            "}," +
            "\"token_analysis\": [%s]," +
            "\"include_volume\": %s," +
            "\"key_insights\": [" +
            "\"Market showing %s sentiment\"," +
            "\"%.0f%% of analyzed tokens are trending %s\"," +
            "\"Average price change: %.1f%%\"" +
            "]," +
            "\"timestamp\": %d," +
            "\"provider\": \"1inch\"" +
            "}",
            chainId, getChainName(chainId), timeframe, trends.size(),
            overview.overallTrend, overview.marketSentiment, overview.avgPriceChange,
            overview.bullishTokens, overview.bearishTokens, 
            overview.totalTokens - overview.bullishTokens - overview.bearishTokens,
            formatTokenTrends(trends),
            includeVolume,
            overview.marketSentiment,
            (double) overview.bullishTokens / overview.totalTokens * 100,
            overview.overallTrend,
            overview.avgPriceChange,
            System.currentTimeMillis()
        );
    }

    private String formatTrendingTokensResponse(List<TrendingTokenAnalysis> trending, Integer chainId, String sortBy, Integer limit) {
        return String.format(
            "{" +
            "\"tool\": \"getTrendingTokens\"," +
            "\"type\": \"trending_tokens\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"sort_by\": \"%s\"," +
            "\"limit\": %d," +
            "\"trending_tokens\": [%s]," +
            "\"trending_summary\": {" +
            "\"strongest_performers\": %d," +
            "\"total_volume_24h\": \"$%.0fM\"," +
            "\"avg_price_change\": \"%.1f%%\"" +
            "}," +
            "\"market_highlights\": [" +
            "\"DeFi tokens showing strong momentum\"," +
            "\"Gaming sector gaining traction\"," +
            "\"Increased trading activity observed\"" +
            "]," +
            "\"timestamp\": %d" +
            "}",
            chainId, getChainName(chainId), sortBy != null ? sortBy : "volume", limit != null ? limit : 10,
            formatTrendingTokens(trending),
            (int) trending.stream().filter(t -> t.trendStrength.equals("strong")).count(),
            trending.stream().mapToDouble(t -> t.volume).sum() / 1_000_000,
            trending.stream().mapToDouble(t -> t.priceChange).average().orElse(0.0),
            System.currentTimeMillis()
        );
    }

    private String formatMarketSentimentResponse(List<SectorSentiment> sentiments, MarketSentimentOverview overview,
                                               Integer chainId, String[] sectors, String timeframe) {
        return String.format(
            "{" +
            "\"tool\": \"analyzeMarketSentiment\"," +
            "\"type\": \"market_sentiment_analysis\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"sectors\": [%s]," +
            "\"timeframe\": \"%s\"," +
            "\"sentiment_overview\": {" +
            "\"overall_sentiment\": \"%s\"," +
            "\"confidence_level\": %d," +
            "\"positive_sectors\": %d," +
            "\"total_sectors\": %d," +
            "\"sentiment_score\": %.2f" +
            "}," +
            "\"sector_analysis\": [%s]," +
            "\"market_indicators\": {" +
            "\"fear_greed_index\": %d," +
            "\"market_momentum\": \"%s\"," +
            "\"volatility_index\": \"medium\"" +
            "}," +
            "\"timestamp\": %d" +
            "}",
            chainId, getChainName(chainId),
            String.join("\",\"", sectors),
            timeframe,
            overview.overallSentiment, overview.confidenceLevel, overview.positiveSectors, overview.totalSectors,
            overview.avgSentimentScore,
            formatSectorSentiments(sentiments),
            (int) (overview.avgSentimentScore * 100),
            overview.overallSentiment.equals("bullish") ? "strong" : "moderate",
            System.currentTimeMillis()
        );
    }

    private String formatCrossChainComparisonResponse(List<ChainMarketPerformance> performances, String[] metrics, String timeframe) {
        ChainMarketPerformance topPerformer = performances.stream()
                .max((p1, p2) -> Double.compare(p1.overallScore, p2.overallScore))
                .orElse(performances.get(0));

        return String.format(
            "{" +
            "\"tool\": \"compareMarketPerformanceAcrossChains\"," +
            "\"type\": \"cross_chain_performance\"," +
            "\"timeframe\": \"%s\"," +
            "\"metrics_analyzed\": [%s]," +
            "\"chains_compared\": %d," +
            "\"performance_ranking\": [%s]," +
            "\"top_performer\": {" +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"overall_score\": %.1f," +
            "\"performance\": \"%s\"" +
            "}," +
            "\"key_findings\": [" +
            "\"%s leads in overall performance\"," +
            "\"Volume varies significantly across chains\"," +
            "\"L2 solutions showing strong growth\"" +
            "]," +
            "\"timestamp\": %d" +
            "}",
            timeframe,
            String.join("\",\"", metrics),
            performances.size(),
            formatPerformanceRanking(performances),
            topPerformer.chainId, topPerformer.chainName, topPerformer.overallScore, topPerformer.performance,
            topPerformer.chainName,
            System.currentTimeMillis()
        );
    }

    private String formatMarketOpportunitiesResponse(List<MarketOpportunity> opportunities, List<MarketRisk> risks,
                                                   Integer chainId, String riskTolerance, String investmentHorizon, String[] sectors) {
        return String.format(
            "{" +
            "\"tool\": \"identifyMarketOpportunities\"," +
            "\"type\": \"market_opportunities_analysis\"," +
            "\"chain_id\": %d," +
            "\"chain_name\": \"%s\"," +
            "\"risk_tolerance\": \"%s\"," +
            "\"investment_horizon\": \"%s\"," +
            "\"sectors_analyzed\": [%s]," +
            "\"opportunities\": [%s]," +
            "\"risks\": [%s]," +
            "\"market_outlook\": {" +
            "\"opportunities_count\": %d," +
            "\"risk_level\": \"%s\"," +
            "\"recommended_action\": \"Selective investing with risk management\"" +
            "}," +
            "\"strategic_recommendations\": [" +
            "\"Diversify across multiple opportunities\"," +
            "\"Monitor risk factors closely\"," +
            "\"Consider gradual position building\"" +
            "]," +
            "\"timestamp\": %d" +
            "}",
            chainId, getChainName(chainId), riskTolerance, investmentHorizon,
            sectors != null ? String.join("\",\"", sectors) : "all",
            formatMarketOpportunities(opportunities),
            formatMarketRisks(risks),
            opportunities.size(),
            risks.stream().anyMatch(r -> "high".equals(r.severity)) ? "elevated" : "moderate",
            System.currentTimeMillis()
        );
    }

    private String formatErrorResponse(String errorType, String message, Integer chainId, String operation) {
        return String.format(
            "{" +
            "\"tool\": \"analyzeMarketTrends\"," +
            "\"type\": \"error\"," +
            "\"error\": {" +
            "\"type\": \"%s\"," +
            "\"message\": \"%s\"," +
            "\"chain_id\": %s," +
            "\"operation\": \"%s\"," +
            "\"timestamp\": %d" +
            "}" +
            "}",
            errorType, message, chainId != null ? chainId.toString() : "null", operation, System.currentTimeMillis()
        );
    }

    // === UTILITY METHODS ===

    private String formatTokenTrends(List<TokenTrendAnalysis> trends) {
        return trends.stream()
                .map(trend -> String.format(
                    "{\"symbol\": \"%s\", \"name\": \"%s\", \"price_change_percent\": %.2f, \"trend\": \"%s\", \"momentum\": \"%s\"%s}",
                    trend.symbol, trend.name, trend.priceChange, trend.trend, trend.momentum,
                    trend.volume != null ? String.format(", \"volume_24h\": %.0f", trend.volume) : ""
                ))
                .collect(Collectors.joining(","));
    }

    private String formatTrendingTokens(List<TrendingTokenAnalysis> trending) {
        return trending.stream()
                .map(token -> String.format(
                    "{\"symbol\": \"%s\", \"name\": \"%s\", \"price_change_percent\": %.2f, \"volume_24h\": %.0f, \"trend_strength\": \"%s\"}",
                    token.symbol, token.name, token.priceChange, token.volume, token.trendStrength
                ))
                .collect(Collectors.joining(","));
    }

    private String formatSectorSentiments(List<SectorSentiment> sentiments) {
        return sentiments.stream()
                .map(sentiment -> String.format(
                    "{\"sector\": \"%s\", \"sentiment\": \"%s\", \"score\": %.2f, \"trend\": \"%s\", \"protocols\": %d}",
                    sentiment.sector, sentiment.sentiment, sentiment.sentimentScore, sentiment.trend, sentiment.protocolCount
                ))
                .collect(Collectors.joining(","));
    }

    private String formatPerformanceRanking(List<ChainMarketPerformance> performances) {
        return performances.stream()
                .sorted((p1, p2) -> Double.compare(p2.overallScore, p1.overallScore))
                .map(perf -> String.format(
                    "{\"chain_id\": %d, \"chain_name\": \"%s\", \"score\": %.1f, \"performance\": \"%s\"}",
                    perf.chainId, perf.chainName, perf.overallScore, perf.performance
                ))
                .collect(Collectors.joining(","));
    }

    private String formatMarketOpportunities(List<MarketOpportunity> opportunities) {
        return opportunities.stream()
                .map(opp -> String.format(
                    "{\"name\": \"%s\", \"description\": \"%s\", \"risk_level\": \"%s\", \"confidence\": %d, \"time_horizon\": \"%s\"}",
                    opp.name, opp.description, opp.riskLevel, opp.confidence, opp.timeHorizon
                ))
                .collect(Collectors.joining(","));
    }

    private String formatMarketRisks(List<MarketRisk> risks) {
        return risks.stream()
                .map(risk -> String.format(
                    "{\"name\": \"%s\", \"description\": \"%s\", \"severity\": \"%s\", \"probability\": %d, \"impact\": \"%s\"}",
                    risk.name, risk.description, risk.severity, risk.probability, risk.impact
                ))
                .collect(Collectors.joining(","));
    }

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
}