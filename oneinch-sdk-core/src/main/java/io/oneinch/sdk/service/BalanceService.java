package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.balance.*;
import io.reactivex.rxjava3.core.Single;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for 1inch Balance API operations
 */
public interface BalanceService {

    // Basic balance operations

    /**
     * Get balances of tokens for wallet address (synchronous)
     * @param request Balance request parameters
     * @return Map of token address to balance amount
     * @throws OneInchException if the request fails
     */
    Map<String, BigInteger> getBalances(BalanceRequest request) throws OneInchException;

    /**
     * Get balances of tokens for wallet address (asynchronous)
     * @param request Balance request parameters
     * @return CompletableFuture with map of token address to balance amount
     */
    CompletableFuture<Map<String, BigInteger>> getBalancesAsync(BalanceRequest request);

    /**
     * Get balances of tokens for wallet address (reactive)
     * @param request Balance request parameters
     * @return Single with map of token address to balance amount
     */
    Single<Map<String, BigInteger>> getBalancesRx(BalanceRequest request);

    // Custom balance operations

    /**
     * Get balances of custom tokens for wallet address (synchronous)
     * @param request Custom balance request parameters
     * @return Map of token address to balance amount
     * @throws OneInchException if the request fails
     */
    Map<String, BigInteger> getCustomBalances(CustomBalanceRequest request) throws OneInchException;

    /**
     * Get balances of custom tokens for wallet address (asynchronous)
     * @param request Custom balance request parameters
     * @return CompletableFuture with map of token address to balance amount
     */
    CompletableFuture<Map<String, BigInteger>> getCustomBalancesAsync(CustomBalanceRequest request);

    /**
     * Get balances of custom tokens for wallet address (reactive)
     * @param request Custom balance request parameters
     * @return Single with map of token address to balance amount
     */
    Single<Map<String, BigInteger>> getCustomBalancesRx(CustomBalanceRequest request);

    // Allowance operations

    /**
     * Get allowances of tokens by spender for wallet address (synchronous)
     * @param request Allowance request parameters
     * @return Map of token address to allowance amount
     * @throws OneInchException if the request fails
     */
    Map<String, BigInteger> getAllowances(AllowanceBalanceRequest request) throws OneInchException;

    /**
     * Get allowances of tokens by spender for wallet address (asynchronous)
     * @param request Allowance request parameters
     * @return CompletableFuture with map of token address to allowance amount
     */
    CompletableFuture<Map<String, BigInteger>> getAllowancesAsync(AllowanceBalanceRequest request);

    /**
     * Get allowances of tokens by spender for wallet address (reactive)
     * @param request Allowance request parameters
     * @return Single with map of token address to allowance amount
     */
    Single<Map<String, BigInteger>> getAllowancesRx(AllowanceBalanceRequest request);

    // Custom allowance operations

    /**
     * Get allowances of custom tokens by spender for wallet address (synchronous)
     * @param request Custom allowance request parameters
     * @return Map of token address to allowance amount
     * @throws OneInchException if the request fails
     */
    Map<String, BigInteger> getCustomAllowances(CustomAllowanceBalanceRequest request) throws OneInchException;

    /**
     * Get allowances of custom tokens by spender for wallet address (asynchronous)
     * @param request Custom allowance request parameters
     * @return CompletableFuture with map of token address to allowance amount
     */
    CompletableFuture<Map<String, BigInteger>> getCustomAllowancesAsync(CustomAllowanceBalanceRequest request);

    /**
     * Get allowances of custom tokens by spender for wallet address (reactive)
     * @param request Custom allowance request parameters
     * @return Single with map of token address to allowance amount
     */
    Single<Map<String, BigInteger>> getCustomAllowancesRx(CustomAllowanceBalanceRequest request);

    // Combined balance and allowance operations

    /**
     * Get balances and allowances of tokens by spender for wallet address (synchronous)
     * @param request Allowance request parameters
     * @return Map of token address to balance and allowance item
     * @throws OneInchException if the request fails
     */
    Map<String, BalanceAndAllowanceItem> getAllowancesAndBalances(AllowanceBalanceRequest request) throws OneInchException;

    /**
     * Get balances and allowances of tokens by spender for wallet address (asynchronous)
     * @param request Allowance request parameters
     * @return CompletableFuture with map of token address to balance and allowance item
     */
    CompletableFuture<Map<String, BalanceAndAllowanceItem>> getAllowancesAndBalancesAsync(AllowanceBalanceRequest request);

    /**
     * Get balances and allowances of tokens by spender for wallet address (reactive)
     * @param request Allowance request parameters
     * @return Single with map of token address to balance and allowance item
     */
    Single<Map<String, BalanceAndAllowanceItem>> getAllowancesAndBalancesRx(AllowanceBalanceRequest request);

    // Custom combined operations

    /**
     * Get balances and allowances of custom tokens by spender for wallet address (synchronous)
     * @param request Custom allowance request parameters
     * @return Map of token address to balance and allowance item
     * @throws OneInchException if the request fails
     */
    Map<String, BalanceAndAllowanceItem> getCustomAllowancesAndBalances(CustomAllowanceBalanceRequest request) throws OneInchException;

    /**
     * Get balances and allowances of custom tokens by spender for wallet address (asynchronous)
     * @param request Custom allowance request parameters
     * @return CompletableFuture with map of token address to balance and allowance item
     */
    CompletableFuture<Map<String, BalanceAndAllowanceItem>> getCustomAllowancesAndBalancesAsync(CustomAllowanceBalanceRequest request);

    /**
     * Get balances and allowances of custom tokens by spender for wallet address (reactive)
     * @param request Custom allowance request parameters
     * @return Single with map of token address to balance and allowance item
     */
    Single<Map<String, BalanceAndAllowanceItem>> getCustomAllowancesAndBalancesRx(CustomAllowanceBalanceRequest request);

    // Multi-wallet operations

    /**
     * Get aggregated balances and allowances by spender for multiple wallets (synchronous)
     * @param request Aggregated balance request parameters
     * @return List of aggregated balance responses
     * @throws OneInchException if the request fails
     */
    List<AggregatedBalanceResponse> getAggregatedBalancesAndAllowances(AggregatedBalanceRequest request) throws OneInchException;

    /**
     * Get aggregated balances and allowances by spender for multiple wallets (asynchronous)
     * @param request Aggregated balance request parameters
     * @return CompletableFuture with list of aggregated balance responses
     */
    CompletableFuture<List<AggregatedBalanceResponse>> getAggregatedBalancesAndAllowancesAsync(AggregatedBalanceRequest request);

    /**
     * Get aggregated balances and allowances by spender for multiple wallets (reactive)
     * @param request Aggregated balance request parameters
     * @return Single with list of aggregated balance responses
     */
    Single<List<AggregatedBalanceResponse>> getAggregatedBalancesAndAllowancesRx(AggregatedBalanceRequest request);

    /**
     * Get balances of custom tokens for multiple wallets (synchronous)
     * @param request Multi-wallet balance request parameters
     * @return Map of wallet address to map of token address to balance
     * @throws OneInchException if the request fails
     */
    Map<String, Map<String, BigInteger>> getBalancesByMultipleWallets(MultiWalletBalanceRequest request) throws OneInchException;

    /**
     * Get balances of custom tokens for multiple wallets (asynchronous)
     * @param request Multi-wallet balance request parameters
     * @return CompletableFuture with map of wallet address to map of token address to balance
     */
    CompletableFuture<Map<String, Map<String, BigInteger>>> getBalancesByMultipleWalletsAsync(MultiWalletBalanceRequest request);

    /**
     * Get balances of custom tokens for multiple wallets (reactive)
     * @param request Multi-wallet balance request parameters
     * @return Single with map of wallet address to map of token address to balance
     */
    Single<Map<String, Map<String, BigInteger>>> getBalancesByMultipleWalletsRx(MultiWalletBalanceRequest request);
}