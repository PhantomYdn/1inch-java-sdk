package io.oneinch.sdk.service;

import io.oneinch.sdk.client.OneInchBalanceApiService;
import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of BalanceService for 1inch Balance API operations
 */
@Slf4j
public class BalanceServiceImpl implements BalanceService {

    private final OneInchBalanceApiService balanceApiService;

    public BalanceServiceImpl(OneInchBalanceApiService balanceApiService) {
        this.balanceApiService = balanceApiService;
    }

    // Basic balance operations

    @Override
    public Map<String, String> getBalances(BalanceRequest request) throws OneInchException {
        try {
            log.debug("Getting balances for wallet {} on chain {}", request.getWalletAddress(), request.getChainId());
            return balanceApiService.getBalances(request.getChainId(), request.getWalletAddress())
                    .blockingGet();
        } catch (Exception e) {
            log.error("Failed to get balances for wallet {} on chain {}", request.getWalletAddress(), request.getChainId(), e);
            throw new OneInchException("Failed to get balances", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, String>> getBalancesAsync(BalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getBalances(request);
            } catch (OneInchException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Single<Map<String, String>> getBalancesRx(BalanceRequest request) {
        log.debug("Getting balances (reactive) for wallet {} on chain {}", request.getWalletAddress(), request.getChainId());
        return balanceApiService.getBalances(request.getChainId(), request.getWalletAddress())
                .doOnError(error -> log.error("Failed to get balances (reactive) for wallet {} on chain {}", 
                        request.getWalletAddress(), request.getChainId(), error));
    }

    // Custom balance operations

    @Override
    public Map<String, String> getCustomBalances(CustomBalanceRequest request) throws OneInchException {
        try {
            log.debug("Getting custom balances for wallet {} on chain {} for {} tokens", 
                    request.getWalletAddress(), request.getChainId(), request.getTokens().size());
            CustomTokensBalanceRequest tokenRequest = CustomTokensBalanceRequest.builder()
                    .tokens(request.getTokens())
                    .build();
            return balanceApiService.getCustomBalances(request.getChainId(), request.getWalletAddress(), tokenRequest)
                    .blockingGet();
        } catch (Exception e) {
            log.error("Failed to get custom balances for wallet {} on chain {}", 
                    request.getWalletAddress(), request.getChainId(), e);
            throw new OneInchException("Failed to get custom balances", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, String>> getCustomBalancesAsync(CustomBalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getCustomBalances(request);
            } catch (OneInchException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Single<Map<String, String>> getCustomBalancesRx(CustomBalanceRequest request) {
        log.debug("Getting custom balances (reactive) for wallet {} on chain {} for {} tokens", 
                request.getWalletAddress(), request.getChainId(), request.getTokens().size());
        CustomTokensBalanceRequest tokenRequest = CustomTokensBalanceRequest.builder()
                .tokens(request.getTokens())
                .build();
        return balanceApiService.getCustomBalances(request.getChainId(), request.getWalletAddress(), tokenRequest)
                .doOnError(error -> log.error("Failed to get custom balances (reactive) for wallet {} on chain {}", 
                        request.getWalletAddress(), request.getChainId(), error));
    }

    // Allowance operations

    @Override
    public Map<String, String> getAllowances(AllowanceBalanceRequest request) throws OneInchException {
        try {
            log.debug("Getting allowances for wallet {} spender {} on chain {}", 
                    request.getWalletAddress(), request.getSpender(), request.getChainId());
            return balanceApiService.getAllowances(request.getChainId(), request.getSpender(), request.getWalletAddress())
                    .blockingGet();
        } catch (Exception e) {
            log.error("Failed to get allowances for wallet {} spender {} on chain {}", 
                    request.getWalletAddress(), request.getSpender(), request.getChainId(), e);
            throw new OneInchException("Failed to get allowances", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, String>> getAllowancesAsync(AllowanceBalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getAllowances(request);
            } catch (OneInchException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Single<Map<String, String>> getAllowancesRx(AllowanceBalanceRequest request) {
        log.debug("Getting allowances (reactive) for wallet {} spender {} on chain {}", 
                request.getWalletAddress(), request.getSpender(), request.getChainId());
        return balanceApiService.getAllowances(request.getChainId(), request.getSpender(), request.getWalletAddress())
                .doOnError(error -> log.error("Failed to get allowances (reactive) for wallet {} spender {} on chain {}", 
                        request.getWalletAddress(), request.getSpender(), request.getChainId(), error));
    }

    // Custom allowance operations

    @Override
    public Map<String, String> getCustomAllowances(CustomAllowanceBalanceRequest request) throws OneInchException {
        try {
            log.debug("Getting custom allowances for wallet {} spender {} on chain {} for {} tokens", 
                    request.getWalletAddress(), request.getSpender(), request.getChainId(), request.getTokens().size());
            CustomTokensBalanceRequest tokenRequest = CustomTokensBalanceRequest.builder()
                    .tokens(request.getTokens())
                    .build();
            return balanceApiService.getCustomAllowances(request.getChainId(), request.getSpender(), 
                    request.getWalletAddress(), tokenRequest)
                    .blockingGet();
        } catch (Exception e) {
            log.error("Failed to get custom allowances for wallet {} spender {} on chain {}", 
                    request.getWalletAddress(), request.getSpender(), request.getChainId(), e);
            throw new OneInchException("Failed to get custom allowances", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, String>> getCustomAllowancesAsync(CustomAllowanceBalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getCustomAllowances(request);
            } catch (OneInchException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Single<Map<String, String>> getCustomAllowancesRx(CustomAllowanceBalanceRequest request) {
        log.debug("Getting custom allowances (reactive) for wallet {} spender {} on chain {} for {} tokens", 
                request.getWalletAddress(), request.getSpender(), request.getChainId(), request.getTokens().size());
        CustomTokensBalanceRequest tokenRequest = CustomTokensBalanceRequest.builder()
                .tokens(request.getTokens())
                .build();
        return balanceApiService.getCustomAllowances(request.getChainId(), request.getSpender(), 
                request.getWalletAddress(), tokenRequest)
                .doOnError(error -> log.error("Failed to get custom allowances (reactive) for wallet {} spender {} on chain {}", 
                        request.getWalletAddress(), request.getSpender(), request.getChainId(), error));
    }

    // Combined balance and allowance operations

    @Override
    public Map<String, BalanceAndAllowanceItem> getAllowancesAndBalances(AllowanceBalanceRequest request) throws OneInchException {
        try {
            log.debug("Getting allowances and balances for wallet {} spender {} on chain {}", 
                    request.getWalletAddress(), request.getSpender(), request.getChainId());
            return balanceApiService.getAllowancesAndBalances(request.getChainId(), request.getSpender(), request.getWalletAddress())
                    .blockingGet();
        } catch (Exception e) {
            log.error("Failed to get allowances and balances for wallet {} spender {} on chain {}", 
                    request.getWalletAddress(), request.getSpender(), request.getChainId(), e);
            throw new OneInchException("Failed to get allowances and balances", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, BalanceAndAllowanceItem>> getAllowancesAndBalancesAsync(AllowanceBalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getAllowancesAndBalances(request);
            } catch (OneInchException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Single<Map<String, BalanceAndAllowanceItem>> getAllowancesAndBalancesRx(AllowanceBalanceRequest request) {
        log.debug("Getting allowances and balances (reactive) for wallet {} spender {} on chain {}", 
                request.getWalletAddress(), request.getSpender(), request.getChainId());
        return balanceApiService.getAllowancesAndBalances(request.getChainId(), request.getSpender(), request.getWalletAddress())
                .doOnError(error -> log.error("Failed to get allowances and balances (reactive) for wallet {} spender {} on chain {}", 
                        request.getWalletAddress(), request.getSpender(), request.getChainId(), error));
    }

    // Custom combined operations

    @Override
    public Map<String, BalanceAndAllowanceItem> getCustomAllowancesAndBalances(CustomAllowanceBalanceRequest request) throws OneInchException {
        try {
            log.debug("Getting custom allowances and balances for wallet {} spender {} on chain {} for {} tokens", 
                    request.getWalletAddress(), request.getSpender(), request.getChainId(), request.getTokens().size());
            CustomTokensBalanceRequest tokenRequest = CustomTokensBalanceRequest.builder()
                    .tokens(request.getTokens())
                    .build();
            return balanceApiService.getCustomAllowancesAndBalances(request.getChainId(), request.getSpender(), 
                    request.getWalletAddress(), tokenRequest)
                    .blockingGet();
        } catch (Exception e) {
            log.error("Failed to get custom allowances and balances for wallet {} spender {} on chain {}", 
                    request.getWalletAddress(), request.getSpender(), request.getChainId(), e);
            throw new OneInchException("Failed to get custom allowances and balances", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, BalanceAndAllowanceItem>> getCustomAllowancesAndBalancesAsync(CustomAllowanceBalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getCustomAllowancesAndBalances(request);
            } catch (OneInchException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Single<Map<String, BalanceAndAllowanceItem>> getCustomAllowancesAndBalancesRx(CustomAllowanceBalanceRequest request) {
        log.debug("Getting custom allowances and balances (reactive) for wallet {} spender {} on chain {} for {} tokens", 
                request.getWalletAddress(), request.getSpender(), request.getChainId(), request.getTokens().size());
        CustomTokensBalanceRequest tokenRequest = CustomTokensBalanceRequest.builder()
                .tokens(request.getTokens())
                .build();
        return balanceApiService.getCustomAllowancesAndBalances(request.getChainId(), request.getSpender(), 
                request.getWalletAddress(), tokenRequest)
                .doOnError(error -> log.error("Failed to get custom allowances and balances (reactive) for wallet {} spender {} on chain {}", 
                        request.getWalletAddress(), request.getSpender(), request.getChainId(), error));
    }

    // Multi-wallet operations

    @Override
    public List<AggregatedBalanceResponse> getAggregatedBalancesAndAllowances(AggregatedBalanceRequest request) throws OneInchException {
        try {
            log.debug("Getting aggregated balances and allowances for {} wallets spender {} on chain {}", 
                    request.getWallets().size(), request.getSpender(), request.getChainId());
            return balanceApiService.getAggregatedBalancesAndAllowances(request.getChainId(), request.getSpender(), 
                    request.getWallets(), request.getFilterEmpty())
                    .blockingGet();
        } catch (Exception e) {
            log.error("Failed to get aggregated balances and allowances for {} wallets spender {} on chain {}", 
                    request.getWallets().size(), request.getSpender(), request.getChainId(), e);
            throw new OneInchException("Failed to get aggregated balances and allowances", e);
        }
    }

    @Override
    public CompletableFuture<List<AggregatedBalanceResponse>> getAggregatedBalancesAndAllowancesAsync(AggregatedBalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getAggregatedBalancesAndAllowances(request);
            } catch (OneInchException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Single<List<AggregatedBalanceResponse>> getAggregatedBalancesAndAllowancesRx(AggregatedBalanceRequest request) {
        log.debug("Getting aggregated balances and allowances (reactive) for {} wallets spender {} on chain {}", 
                request.getWallets().size(), request.getSpender(), request.getChainId());
        return balanceApiService.getAggregatedBalancesAndAllowances(request.getChainId(), request.getSpender(), 
                request.getWallets(), request.getFilterEmpty())
                .doOnError(error -> log.error("Failed to get aggregated balances and allowances (reactive) for {} wallets spender {} on chain {}", 
                        request.getWallets().size(), request.getSpender(), request.getChainId(), error));
    }

    @Override
    public Map<String, Map<String, String>> getBalancesByMultipleWallets(MultiWalletBalanceRequest request) throws OneInchException {
        try {
            log.debug("Getting balances for {} wallets and {} tokens on chain {}", 
                    request.getWallets().size(), request.getTokens().size(), request.getChainId());
            CustomTokensAndWalletsBalanceRequest requestBody = CustomTokensAndWalletsBalanceRequest.builder()
                    .tokens(request.getTokens())
                    .wallets(request.getWallets())
                    .build();
            return balanceApiService.getBalancesByMultipleWallets(request.getChainId(), requestBody)
                    .blockingGet();
        } catch (Exception e) {
            log.error("Failed to get balances for {} wallets and {} tokens on chain {}", 
                    request.getWallets().size(), request.getTokens().size(), request.getChainId(), e);
            throw new OneInchException("Failed to get balances for multiple wallets", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, Map<String, String>>> getBalancesByMultipleWalletsAsync(MultiWalletBalanceRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getBalancesByMultipleWallets(request);
            } catch (OneInchException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Single<Map<String, Map<String, String>>> getBalancesByMultipleWalletsRx(MultiWalletBalanceRequest request) {
        log.debug("Getting balances (reactive) for {} wallets and {} tokens on chain {}", 
                request.getWallets().size(), request.getTokens().size(), request.getChainId());
        CustomTokensAndWalletsBalanceRequest requestBody = CustomTokensAndWalletsBalanceRequest.builder()
                .tokens(request.getTokens())
                .wallets(request.getWallets())
                .build();
        return balanceApiService.getBalancesByMultipleWallets(request.getChainId(), requestBody)
                .doOnError(error -> log.error("Failed to get balances (reactive) for {} wallets and {} tokens on chain {}", 
                        request.getWallets().size(), request.getTokens().size(), request.getChainId(), error));
    }
}