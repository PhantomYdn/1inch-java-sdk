package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchErrorHandler;
import io.oneinch.sdk.client.OneInchPriceApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.price.Currency;
import io.oneinch.sdk.model.price.PostPriceRequest;
import io.oneinch.sdk.model.price.PriceRequest;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {
    
    private final OneInchPriceApiService apiService;
    
    // ==================== WHITELIST PRICES ====================
    
    @Override
    public Single<Map<String, BigInteger>> getWhitelistPricesRx(Integer chainId, Currency currency) {
        log.info("Getting whitelist prices (reactive) for chain {} in currency {}", chainId, currency);
        
        String currencyStr = currency != null ? currency.name() : null;
        
        return apiService.getWhitelistPrices(chainId, currencyStr)
                .doOnSuccess(prices -> log.debug("Retrieved {} whitelist prices", prices.size()))
                .doOnError(error -> log.error("Whitelist prices request failed for chain {}", chainId, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<Map<String, BigInteger>> getWhitelistPricesAsync(Integer chainId, Currency currency) {
        return getWhitelistPricesRx(chainId, currency).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public Map<String, BigInteger> getWhitelistPrices(Integer chainId, Currency currency) throws OneInchException {
        try {
            return getWhitelistPricesRx(chainId, currency).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== SPECIFIC TOKEN PRICES ====================
    
    @Override
    public Single<Map<String, BigInteger>> getPricesRx(PriceRequest request) {
        log.info("Getting prices (reactive) for {} addresses on chain {} in currency {}", 
                request.getAddresses() != null ? request.getAddresses().size() : "all", 
                request.getChainId(), 
                request.getCurrency());
        
        // If no specific addresses, get all whitelist prices
        if (request.getAddresses() == null || request.getAddresses().isEmpty()) {
            return getWhitelistPricesRx(request.getChainId(), request.getCurrency());
        }
        
        String currencyStr = request.getCurrency() != null ? request.getCurrency().name() : null;
        
        // Use POST method for multiple addresses to avoid URL length issues
        if (request.getAddresses().size() > 1) {
            PostPriceRequest postRequest = PostPriceRequest.builder()
                    .tokens(request.getAddresses())
                    .currency(currencyStr)
                    .build();
                    
            return apiService.getPricesByPost(request.getChainId(), postRequest)
                    .doOnSuccess(prices -> log.debug("Retrieved {} prices via POST", prices.size()))
                    .doOnError(error -> log.error("POST prices request failed", error))
                    .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
        } else {
            // Single address - use GET method
            String address = request.getAddresses().get(0);
            return apiService.getPricesByAddresses(request.getChainId(), address, currencyStr)
                    .doOnSuccess(prices -> log.debug("Retrieved price for address {}", address))
                    .doOnError(error -> log.error("GET price request failed for address {}", address, error))
                    .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
        }
    }
    
    @Override
    public CompletableFuture<Map<String, BigInteger>> getPricesAsync(PriceRequest request) {
        return getPricesRx(request).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public Map<String, BigInteger> getPrices(PriceRequest request) throws OneInchException {
        try {
            return getPricesRx(request).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== SINGLE TOKEN PRICE ====================
    
    @Override
    public Single<BigInteger> getPriceRx(Integer chainId, String address, Currency currency) {
        log.info("Getting price (reactive) for address {} on chain {} in currency {}", address, chainId, currency);
        
        PriceRequest request = PriceRequest.builder()
                .chainId(chainId)
                .addresses(List.of(address))
                .currency(currency)
                .build();
        
        return getPricesRx(request)
                .map(prices -> {
                    BigInteger price = prices.get(address);
                    if (price == null) {
                        throw new OneInchException("Price not found for address: " + address);
                    }
                    return price;
                })
                .doOnSuccess(price -> log.debug("Retrieved price {} for address {}", price, address))
                .doOnError(error -> log.error("Single price request failed for address {}", address, error));
    }
    
    @Override
    public CompletableFuture<BigInteger> getPriceAsync(Integer chainId, String address, Currency currency) {
        return getPriceRx(chainId, address, currency).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public BigInteger getPrice(Integer chainId, String address, Currency currency) throws OneInchException {
        try {
            return getPriceRx(chainId, address, currency).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
    
    // ==================== SUPPORTED CURRENCIES ====================
    
    @Override
    public Single<List<String>> getSupportedCurrenciesRx(Integer chainId) {
        log.info("Getting supported currencies (reactive) for chain {}", chainId);
        
        return apiService.getSupportedCurrencies(chainId)
                .map(response -> response.getCodes())
                .doOnSuccess(currencies -> log.debug("Retrieved {} supported currencies", currencies.size()))
                .doOnError(error -> log.error("Supported currencies request failed for chain {}", chainId, error))
                .onErrorResumeNext(error -> Single.error(OneInchErrorHandler.handleError(error)));
    }
    
    @Override
    public CompletableFuture<List<String>> getSupportedCurrenciesAsync(Integer chainId) {
        return getSupportedCurrenciesRx(chainId).toCompletionStage().toCompletableFuture();
    }
    
    @Override
    public List<String> getSupportedCurrencies(Integer chainId) throws OneInchException {
        try {
            return getSupportedCurrenciesRx(chainId).blockingGet();
        } catch (Exception e) {
            throw OneInchErrorHandler.handleError(e);
        }
    }
}