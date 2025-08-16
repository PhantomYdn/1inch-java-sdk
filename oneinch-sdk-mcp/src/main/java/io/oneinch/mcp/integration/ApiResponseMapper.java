package io.oneinch.mcp.integration;

import io.oneinch.sdk.model.*;
import io.oneinch.sdk.model.swap.*;
import io.oneinch.sdk.model.token.*;
import io.oneinch.sdk.model.portfolio.*;
import io.oneinch.sdk.model.balance.*;
import io.oneinch.sdk.model.history.*;
import io.oneinch.sdk.model.tokendetails.CurrentValueResponse;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility service for mapping between 1inch SDK objects and generic Map representations.
 * This enables caching and serialization of complex SDK objects.
 */
@ApplicationScoped
public class ApiResponseMapper {

    private static final Logger log = LoggerFactory.getLogger(ApiResponseMapper.class);

    // === SWAP API MAPPINGS ===

    public Map<String, Object> mapQuoteResponse(QuoteResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("dstAmount", response.getDstAmount());
        map.put("gas", response.getGas());
        
        if (response.getSrcToken() != null) {
            map.put("fromToken", mapToken(response.getSrcToken()));
        }
        
        if (response.getDstToken() != null) {
            map.put("toToken", mapToken(response.getDstToken()));
        }
        
        if (response.getProtocols() != null) {
            map.put("protocols", response.getProtocols());
        }
        
        return map;
    }

    public QuoteResponse unmapQuoteResponse(Map<String, Object> map) {
        QuoteResponse response = new QuoteResponse();
        
        if (map.containsKey("dstAmount")) {
            response.setDstAmount((BigInteger) map.get("dstAmount"));
        }
        
        if (map.containsKey("gas")) {
            response.setGas((BigInteger) map.get("gas"));
        }
        
        // Add more unmapping as needed
        return response;
    }

    public Map<String, Object> mapSwapResponse(SwapResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("dstAmount", response.getDstAmount());
        
        if (response.getTx() != null) {
            map.put("tx", mapTransactionData(response.getTx()));
        }
        
        return map;
    }

    // === TOKEN API MAPPINGS ===

    public Map<String, Object> mapCustomTokens(Map<String, Token> tokens) {
        Map<String, Object> map = new HashMap<>();
        if (tokens != null) {
            map.put("tokens", tokens.entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> mapToken(entry.getValue())
                    )));
        }
        return map;
    }

    public Map<String, Object> mapMultiChainTokens(List<ProviderTokenDto> tokens) {
        Map<String, Object> map = new HashMap<>();
        if (tokens != null) {
            map.put("tokens", tokens.stream()
                    .map(this::mapProviderToken)
                    .collect(Collectors.toList()));
        }
        return map;
    }

    public Map<String, Object> mapTokenList(TokenListResponse response) {
        Map<String, Object> map = new HashMap<>();
        
        if (response.getTokens() != null) {
            map.put("tokens", response.getTokens().stream()
                    .map(this::mapToken)
                    .collect(Collectors.toList()));
        }
        
        if (response.getName() != null) {
            map.put("name", response.getName());
        }
        
        if (response.getVersion() != null) {
            map.put("version", mapVersion(response.getVersion()));
        }
        
        return map;
    }

    public TokenListResponse unmapTokenListResponse(Map<String, Object> map) {
        TokenListResponse response = new TokenListResponse();
        
        if (map.containsKey("tokens") && map.get("tokens") instanceof List) {
            List<Map<String, Object>> tokensList = (List<Map<String, Object>>) map.get("tokens");
            List<Token> tokens = tokensList.stream()
                    .map(this::unmapToken)
                    .collect(Collectors.toList());
            response.setTokens(tokens);
        }
        
        if (map.containsKey("name")) {
            response.setName((String) map.get("name"));
        }
        
        return response;
    }

    public List<Token> unmapTokenList(Map<String, Object> map) {
        if (map.containsKey("tokens") && map.get("tokens") instanceof List) {
            List<Object> tokensList = (List<Object>) map.get("tokens");
            return tokensList.stream()
                    .map(obj -> unmapToken((Map<String, Object>) obj))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public Map<Integer, List<Token>> unmapMultiChainTokens(Map<String, Object> map) {
        Map<Integer, List<Token>> result = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                Integer chainId = Integer.valueOf(entry.getKey());
                if (entry.getValue() instanceof List) {
                    List<Object> tokensList = (List<Object>) entry.getValue();
                    List<Token> tokens = tokensList.stream()
                            .map(obj -> unmapToken((Map<String, Object>) obj))
                            .collect(Collectors.toList());
                    result.put(chainId, tokens);
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid chain ID in multi-chain tokens: {}", entry.getKey());
            }
        }
        
        return result;
    }

    public Map<String, Object> mapMultiChainTokens(Map<Integer, List<Token>> tokensMap) {
        Map<String, Object> map = new HashMap<>();
        
        int totalTokens = 0;
        for (Map.Entry<Integer, List<Token>> entry : tokensMap.entrySet()) {
            List<Map<String, Object>> tokenMaps = entry.getValue().stream()
                    .map(this::mapToken)
                    .collect(Collectors.toList());
            map.put(entry.getKey().toString(), tokenMaps);
            totalTokens += entry.getValue().size();
        }
        
        map.put("total_tokens", totalTokens);
        map.put("supported_chains", tokensMap.keySet());
        
        return map;
    }

    public Map<String, Object> mapToken(Token token) {
        Map<String, Object> map = new HashMap<>();
        map.put("symbol", token.getSymbol());
        map.put("name", token.getName());
        map.put("address", token.getAddress());
        map.put("decimals", token.getDecimals());
        map.put("chainId", token.getChainId());
        
        if (token.getLogoURI() != null) {
            map.put("logoURI", token.getLogoURI());
        }
        
        return map;
    }

    public Token unmapToken(Map<String, Object> map) {
        Token token = new Token();
        token.setSymbol((String) map.get("symbol"));
        token.setName((String) map.get("name"));
        token.setAddress((String) map.get("address"));
        
        if (map.containsKey("decimals")) {
            token.setDecimals((Integer) map.get("decimals"));
        }
        
        if (map.containsKey("chainId")) {
            token.setChainId((Integer) map.get("chainId"));
        }
        
        if (map.containsKey("logoURI")) {
            token.setLogoURI((String) map.get("logoURI"));
        }
        
        return token;
    }

    private Map<String, Object> mapVersion(VersionDto version) {
        Map<String, Object> map = new HashMap<>();
        map.put("major", version.getMajor());
        map.put("minor", version.getMinor());
        map.put("patch", version.getPatch());
        return map;
    }

    public Map<String, Object> mapProviderToken(ProviderTokenDto token) {
        Map<String, Object> map = new HashMap<>();
        map.put("symbol", token.getSymbol());
        map.put("name", token.getName());
        map.put("address", token.getAddress());
        map.put("decimals", token.getDecimals());
        map.put("chainId", token.getChainId());
        map.put("providers", token.getProviders());
        
        return map;
    }

    public ProviderTokenDto unmapProviderTokenDto(Map<String, Object> map) {
        ProviderTokenDto token = new ProviderTokenDto();
        token.setSymbol((String) map.get("symbol"));
        token.setName((String) map.get("name"));
        token.setAddress((String) map.get("address"));
        
        if (map.containsKey("decimals")) {
            token.setDecimals((Integer) map.get("decimals"));
        }
        
        if (map.containsKey("chainId")) {
            token.setChainId((Integer) map.get("chainId"));
        }
        
        return token;
    }

    public Map<String, Object> mapProviderTokenDto(ProviderTokenDto token) {
        Map<String, Object> map = new HashMap<>();
        map.put("symbol", token.getSymbol());
        map.put("name", token.getName());
        map.put("address", token.getAddress());
        map.put("decimals", token.getDecimals());
        map.put("chainId", token.getChainId());
        
        if (token.getTags() != null) {
            map.put("tags", token.getTags().stream()
                    .map(tag -> tag.toString())
                    .collect(Collectors.toList()));
        }
        
        return map;
    }

    // === PORTFOLIO API MAPPINGS ===

    public Map<String, Object> mapCurrentValueResponse(CurrentValueResponse response) {
        Map<String, Object> map = new HashMap<>();
        
        if (response.getTotal() != null) {
            map.put("total", response.getTotal());
        }
        
        if (response.getByAddress() != null) {
            map.put("byAddress", response.getByAddress().stream()
                    .map(this::mapAddressValue)
                    .collect(Collectors.toList()));
        }
        
        if (response.getByChain() != null) {
            map.put("byChain", response.getByChain().stream()
                    .map(this::mapChainValue)
                    .collect(Collectors.toList()));
        }
        
        if (response.getByCategory() != null) {
            map.put("byCategory", response.getByCategory().stream()
                    .map(this::mapCategoryValue)
                    .collect(Collectors.toList()));
        }
        
        return map;
    }

    public CurrentValueResponse unmapCurrentValueResponse(Map<String, Object> map) {
        CurrentValueResponse response = new CurrentValueResponse();
        
        if (map.containsKey("total")) {
            Object total = map.get("total");
            if (total instanceof java.math.BigDecimal) {
                response.setTotal(((java.math.BigDecimal) total).doubleValue());
            } else if (total instanceof Number) {
                response.setTotal(((Number) total).doubleValue());
            }
        }
        
        // Add more unmapping as needed
        return response;
    }

    // === HISTORY API MAPPINGS ===

    public Map<String, Object> mapHistoryResponseDto(HistoryResponseDto response) {
        Map<String, Object> map = new HashMap<>();
        
        if (response.getItems() != null) {
            map.put("items", response.getItems().stream()
                    .map(this::mapHistoryEventDto)
                    .collect(Collectors.toList()));
        }
        
        if (response.getCacheCounter() != null) {
            map.put("cacheCounter", response.getCacheCounter());
        }
        
        return map;
    }

    public Map<String, Object> mapHistoryEventDto(HistoryEventDto event) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", event.getId());
        map.put("type", event.getType() != null ? event.getType().toString() : null);
        map.put("rating", event.getRating() != null ? event.getRating().toString() : null);
        
        if (event.getDetails() != null) {
            map.put("details", mapTransactionDetailsDto(event.getDetails()));
        }
        
        return map;
    }

    // === PRICE API MAPPINGS ===

    public Map<String, Object> mapPrices(Map<String, BigInteger> prices) {
        Map<String, Object> map = new HashMap<>();
        
        if (prices != null) {
            Map<String, String> priceStrings = prices.entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toString()
                    ));
            map.put("prices", priceStrings);
            map.put("count", prices.size());
        }
        
        return map;
    }

    public Map<String, BigInteger> unmapPrices(Map<String, Object> map) {
        Map<String, BigInteger> prices = new HashMap<>();
        
        if (map.containsKey("prices") && map.get("prices") instanceof Map) {
            Map<String, Object> pricesMap = (Map<String, Object>) map.get("prices");
            for (Map.Entry<String, Object> entry : pricesMap.entrySet()) {
                try {
                    BigInteger price = new BigInteger(entry.getValue().toString());
                    prices.put(entry.getKey(), price);
                } catch (NumberFormatException e) {
                    log.warn("Invalid price format for {}: {}", entry.getKey(), entry.getValue());
                }
            }
        }
        
        return prices;
    }

    // === BALANCE API MAPPINGS ===

    public Map<String, Object> mapBalanceData(Map<String, BigInteger> balances) {
        Map<String, Object> map = new HashMap<>();
        map.put("balances", balances);
        map.put("timestamp", System.currentTimeMillis());
        return map;
    }

    // === UTILITY MAPPINGS ===


    private Map<String, Object> mapProtocol(SelectedProtocol protocol) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", protocol.getName());
        map.put("part", protocol.getPart());
        map.put("fromTokenAddress", protocol.getFromTokenAddress());
        map.put("toTokenAddress", protocol.getToTokenAddress());
        return map;
    }

    private Map<String, Object> mapTransactionData(TransactionData tx) {
        Map<String, Object> map = new HashMap<>();
        map.put("from", tx.getFrom());
        map.put("to", tx.getTo());
        map.put("data", tx.getData());
        map.put("value", tx.getValue());
        map.put("gasPrice", tx.getGasPrice());
        map.put("gas", tx.getGas());
        return map;
    }

    private Map<String, Object> mapAddressValue(AddressValue addressValue) {
        Map<String, Object> map = new HashMap<>();
        map.put("address", addressValue.getAddress());
        map.put("valueUsd", addressValue.getValueUsd());
        return map;
    }

    private Map<String, Object> mapChainValue(ChainValue chainValue) {
        Map<String, Object> map = new HashMap<>();
        map.put("chainId", chainValue.getChainId());
        map.put("chainName", chainValue.getChainName());
        map.put("valueUsd", chainValue.getValueUsd());
        return map;
    }

    private Map<String, Object> mapCategoryValue(CategoryValue categoryValue) {
        Map<String, Object> map = new HashMap<>();
        map.put("categoryName", categoryValue.getCategoryName());
        map.put("valueUsd", categoryValue.getValueUsd());
        return map;
    }

    private Map<String, Object> mapResponseMeta(ResponseMeta meta) {
        Map<String, Object> map = new HashMap<>();
        // Add meta mapping as needed based on ResponseMeta structure
        return map;
    }

    private Map<String, Object> mapTransactionDetailsDto(TransactionDetailsDto details) {
        Map<String, Object> map = new HashMap<>();
        map.put("txHash", details.getTxHash());
        map.put("chainId", details.getChainId());
        map.put("blockNumber", details.getBlockNumber());
        map.put("status", details.getStatus() != null ? details.getStatus().toString() : null);
        map.put("type", details.getType() != null ? details.getType().toString() : null);
        
        if (details.getTokenActions() != null) {
            map.put("tokenActions", details.getTokenActions().stream()
                    .map(this::mapTokenActionDto)
                    .collect(Collectors.toList()));
        }
        
        return map;
    }

    private Map<String, Object> mapTokenActionDto(TokenActionDto action) {
        Map<String, Object> map = new HashMap<>();
        map.put("direction", action.getDirection() != null ? action.getDirection().toString() : null);
        map.put("amount", action.getAmount());
        map.put("fromAddress", action.getFromAddress());
        map.put("toAddress", action.getToAddress());
        return map;
    }
}