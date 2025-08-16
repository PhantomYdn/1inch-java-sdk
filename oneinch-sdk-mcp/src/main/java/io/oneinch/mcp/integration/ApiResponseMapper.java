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
import java.util.ArrayList;
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

    // === ADDITIONAL PORTFOLIO API MAPPINGS ===

    public Map<String, Object> mapAdapterResult(AdapterResult adapter) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("chainId", adapter.getChainId());
        map.put("contractAddress", adapter.getContractAddress());
        map.put("address", adapter.getAddress());
        map.put("protocolType", adapter.getProtocolType());
        map.put("protocolHandlerId", adapter.getProtocolHandlerId());
        map.put("protocolGroupId", adapter.getProtocolGroupId());
        map.put("protocolGroupName", adapter.getProtocolGroupName());
        
        if (adapter.getValueUsd() != null) {
            map.put("valueUsd", adapter.getValueUsd());
        }
        
        return map;
    }

    public List<AdapterResult> unmapAdapterResultList(Map<String, Object> map) {
        if (map.containsKey("adapters") && map.get("adapters") instanceof List) {
            List<Object> adaptersList = (List<Object>) map.get("adapters");
            return adaptersList.stream()
                    .map(obj -> unmapAdapterResult((Map<String, Object>) obj))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public AdapterResult unmapAdapterResult(Map<String, Object> map) {
        AdapterResult adapter = new AdapterResult();
        
        if (map.containsKey("chainId")) {
            adapter.setChainId((Integer) map.get("chainId"));
        }
        
        if (map.containsKey("contractAddress")) {
            adapter.setContractAddress((String) map.get("contractAddress"));
        }
        
        if (map.containsKey("address")) {
            adapter.setAddress((String) map.get("address"));
        }
        
        if (map.containsKey("protocolType")) {
            adapter.setProtocolType((String) map.get("protocolType"));
        }
        
        if (map.containsKey("protocolHandlerId")) {
            adapter.setProtocolHandlerId((String) map.get("protocolHandlerId"));
        }
        
        if (map.containsKey("protocolGroupId")) {
            adapter.setProtocolGroupId((String) map.get("protocolGroupId"));
        }
        
        if (map.containsKey("protocolGroupName")) {
            adapter.setProtocolGroupName((String) map.get("protocolGroupName"));
        }
        
        if (map.containsKey("valueUsd")) {
            Object value = map.get("valueUsd");
            if (value instanceof Number) {
                adapter.setValueUsd(((Number) value).doubleValue());
            }
        }
        
        return adapter;
    }

    public Map<String, Object> mapAdapterResultList(List<AdapterResult> adapters) {
        Map<String, Object> map = new HashMap<>();
        
        if (adapters != null) {
            List<Map<String, Object>> adapterMaps = adapters.stream()
                    .map(this::mapAdapterResult)
                    .collect(Collectors.toList());
            map.put("adapters", adapterMaps);
            map.put("count", adapters.size());
        }
        
        return map;
    }

    public Map<String, Object> mapSupportedChainResponse(SupportedChainResponse chain) {
        Map<String, Object> map = new HashMap<>();
        map.put("chainId", chain.getChainId());
        map.put("chainName", chain.getChainName());
        
        if (chain.getChainIcon() != null) {
            map.put("chainIcon", chain.getChainIcon());
        }
        
        if (chain.getNativeToken() != null) {
            map.put("nativeToken", mapToken(chain.getNativeToken()));
        }
        
        return map;
    }

    public List<SupportedChainResponse> unmapSupportedChainsList(Map<String, Object> map) {
        if (map.containsKey("chains") && map.get("chains") instanceof List) {
            List<Object> chainsList = (List<Object>) map.get("chains");
            return chainsList.stream()
                    .map(obj -> unmapSupportedChainResponse((Map<String, Object>) obj))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public SupportedChainResponse unmapSupportedChainResponse(Map<String, Object> map) {
        SupportedChainResponse chain = new SupportedChainResponse();
        
        if (map.containsKey("chainId")) {
            chain.setChainId((Integer) map.get("chainId"));
        }
        
        if (map.containsKey("chainName")) {
            chain.setChainName((String) map.get("chainName"));
        }
        
        if (map.containsKey("chainIcon")) {
            chain.setChainIcon((String) map.get("chainIcon"));
        }
        
        if (map.containsKey("nativeToken") && map.get("nativeToken") instanceof Map) {
            chain.setNativeToken(unmapToken((Map<String, Object>) map.get("nativeToken")));
        }
        
        return chain;
    }

    public Map<String, Object> mapSupportedChainsList(List<SupportedChainResponse> chains) {
        Map<String, Object> map = new HashMap<>();
        
        if (chains != null) {
            List<Map<String, Object>> chainMaps = chains.stream()
                    .map(this::mapSupportedChainResponse)
                    .collect(Collectors.toList());
            map.put("chains", chainMaps);
            map.put("count", chains.size());
        }
        
        return map;
    }

    public Map<String, Object> mapSupportedProtocolGroupResponse(SupportedProtocolGroupResponse protocol) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("chainId", protocol.getChainId());
        map.put("protocolGroupId", protocol.getProtocolGroupId());
        map.put("protocolGroupName", protocol.getProtocolGroupName());
        
        if (protocol.getProtocolGroupIcon() != null) {
            map.put("protocolGroupIcon", protocol.getProtocolGroupIcon());
        }
        
        return map;
    }

    public List<SupportedProtocolGroupResponse> unmapSupportedProtocolsList(Map<String, Object> map) {
        if (map.containsKey("protocols") && map.get("protocols") instanceof List) {
            List<Object> protocolsList = (List<Object>) map.get("protocols");
            return protocolsList.stream()
                    .map(obj -> unmapSupportedProtocolGroupResponse((Map<String, Object>) obj))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public SupportedProtocolGroupResponse unmapSupportedProtocolGroupResponse(Map<String, Object> map) {
        SupportedProtocolGroupResponse protocol = new SupportedProtocolGroupResponse();
        
        if (map.containsKey("chainId")) {
            protocol.setChainId((Integer) map.get("chainId"));
        }
        
        if (map.containsKey("protocolGroupId")) {
            protocol.setProtocolGroupId((String) map.get("protocolGroupId"));
        }
        
        if (map.containsKey("protocolGroupName")) {
            protocol.setProtocolGroupName((String) map.get("protocolGroupName"));
        }
        
        if (map.containsKey("protocolGroupIcon")) {
            protocol.setProtocolGroupIcon((String) map.get("protocolGroupIcon"));
        }
        
        return protocol;
    }

    public Map<String, Object> mapSupportedProtocolsList(List<SupportedProtocolGroupResponse> protocols) {
        Map<String, Object> map = new HashMap<>();
        
        if (protocols != null) {
            List<Map<String, Object>> protocolMaps = protocols.stream()
                    .map(this::mapSupportedProtocolGroupResponse)
                    .collect(Collectors.toList());
            map.put("protocols", protocolMaps);
            map.put("count", protocols.size());
        }
        
        return map;
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
        
        if (balances != null) {
            // Convert BigInteger values to String for serialization
            Map<String, String> balanceStrings = balances.entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toString()
                    ));
            map.put("balances", balanceStrings);
            map.put("count", balances.size());
        } else {
            map.put("balances", new HashMap<>());
            map.put("count", 0);
        }
        
        map.put("timestamp", System.currentTimeMillis());
        return map;
    }

    public Map<String, BigInteger> unmapBalances(Map<String, Object> map) {
        Map<String, BigInteger> balances = new HashMap<>();
        
        if (map.containsKey("balances") && map.get("balances") instanceof Map) {
            Map<String, Object> balancesMap = (Map<String, Object>) map.get("balances");
            for (Map.Entry<String, Object> entry : balancesMap.entrySet()) {
                try {
                    BigInteger balance = new BigInteger(entry.getValue().toString());
                    balances.put(entry.getKey(), balance);
                } catch (NumberFormatException e) {
                    log.warn("Invalid balance format for {}: {}", entry.getKey(), entry.getValue());
                    balances.put(entry.getKey(), BigInteger.ZERO);
                }
            }
        }
        
        return balances;
    }

    public Map<String, Object> mapBalanceAndAllowanceData(Map<String, BalanceAndAllowanceItem> items) {
        Map<String, Object> map = new HashMap<>();
        
        if (items != null) {
            Map<String, Map<String, String>> itemMaps = items.entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> mapBalanceAndAllowanceItem(entry.getValue())
                    ));
            map.put("items", itemMaps);
            map.put("count", items.size());
        } else {
            map.put("items", new HashMap<>());
            map.put("count", 0);
        }
        
        map.put("timestamp", System.currentTimeMillis());
        return map;
    }

    public Map<String, BalanceAndAllowanceItem> unmapBalanceAndAllowanceData(Map<String, Object> map) {
        Map<String, BalanceAndAllowanceItem> items = new HashMap<>();
        
        if (map.containsKey("items") && map.get("items") instanceof Map) {
            Map<String, Object> itemsMap = (Map<String, Object>) map.get("items");
            for (Map.Entry<String, Object> entry : itemsMap.entrySet()) {
                try {
                    BalanceAndAllowanceItem item = unmapBalanceAndAllowanceItem((Map<String, Object>) entry.getValue());
                    items.put(entry.getKey(), item);
                } catch (Exception e) {
                    log.warn("Error unmapping balance and allowance item for {}: {}", entry.getKey(), e.getMessage());
                }
            }
        }
        
        return items;
    }

    private Map<String, String> mapBalanceAndAllowanceItem(BalanceAndAllowanceItem item) {
        Map<String, String> map = new HashMap<>();
        
        if (item.getBalance() != null) {
            map.put("balance", item.getBalance().toString());
        }
        
        if (item.getAllowance() != null) {
            map.put("allowance", item.getAllowance().toString());
        }
        
        return map;
    }

    private BalanceAndAllowanceItem unmapBalanceAndAllowanceItem(Map<String, Object> map) {
        BalanceAndAllowanceItem item = new BalanceAndAllowanceItem();
        
        if (map.containsKey("balance")) {
            try {
                item.setBalance(new BigInteger(map.get("balance").toString()));
            } catch (NumberFormatException e) {
                item.setBalance(BigInteger.ZERO);
            }
        }
        
        if (map.containsKey("allowance")) {
            try {
                item.setAllowance(new BigInteger(map.get("allowance").toString()));
            } catch (NumberFormatException e) {
                item.setAllowance(BigInteger.ZERO);
            }
        }
        
        return item;
    }

    public Map<String, Object> mapAggregatedBalanceResponse(AggregatedBalanceResponse response) {
        Map<String, Object> map = new HashMap<>();
        
        if (response.getAddress() != null) {
            map.put("address", response.getAddress());
        }
        
        if (response.getName() != null) {
            map.put("name", response.getName());
        }
        
        if (response.getSymbol() != null) {
            map.put("symbol", response.getSymbol());
        }
        
        if (response.getDecimals() != null) {
            map.put("decimals", response.getDecimals());
        }
        
        if (response.getWallets() != null) {
            map.put("wallets", response.getWallets());
        }
        
        return map;
    }

    public Map<String, Object> mapAggregatedBalanceList(List<AggregatedBalanceResponse> responses) {
        Map<String, Object> map = new HashMap<>();
        
        if (responses != null) {
            List<Map<String, Object>> responseMaps = responses.stream()
                    .map(this::mapAggregatedBalanceResponse)
                    .collect(Collectors.toList());
            map.put("responses", responseMaps);
            map.put("count", responses.size());
        } else {
            map.put("responses", List.of());
            map.put("count", 0);
        }
        
        return map;
    }

    public List<AggregatedBalanceResponse> unmapAggregatedBalanceList(Map<String, Object> map) {
        List<AggregatedBalanceResponse> responses = new ArrayList<>();
        
        if (map.containsKey("responses") && map.get("responses") instanceof List) {
            List<Object> responsesList = (List<Object>) map.get("responses");
            responses = responsesList.stream()
                    .map(obj -> unmapAggregatedBalanceResponse((Map<String, Object>) obj))
                    .collect(Collectors.toList());
        }
        
        return responses;
    }

    private AggregatedBalanceResponse unmapAggregatedBalanceResponse(Map<String, Object> map) {
        AggregatedBalanceResponse response = new AggregatedBalanceResponse();
        
        if (map.containsKey("address")) {
            response.setAddress((String) map.get("address"));
        }
        
        if (map.containsKey("name")) {
            response.setName((String) map.get("name"));
        }
        
        if (map.containsKey("symbol")) {
            response.setSymbol((String) map.get("symbol"));
        }
        
        if (map.containsKey("decimals")) {
            response.setDecimals((Integer) map.get("decimals"));
        }
        
        if (map.containsKey("wallets") && map.get("wallets") instanceof Map) {
            response.setWallets((Map<String, Object>) map.get("wallets"));
        }
        
        return response;
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