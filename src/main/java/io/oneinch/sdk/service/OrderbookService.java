package io.oneinch.sdk.service;

import io.oneinch.sdk.exception.OneInchException;
import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface OrderbookService {

    // Create Limit Order
    LimitOrderV4Response createLimitOrder(LimitOrderV4Request request) throws OneInchException;
    CompletableFuture<LimitOrderV4Response> createLimitOrderAsync(LimitOrderV4Request request);
    Single<LimitOrderV4Response> createLimitOrderRx(LimitOrderV4Request request);

    // Get Limit Orders by Address
    List<GetLimitOrdersV4Response> getLimitOrdersByAddress(GetLimitOrdersRequest request) throws OneInchException;
    CompletableFuture<List<GetLimitOrdersV4Response>> getLimitOrdersByAddressAsync(GetLimitOrdersRequest request);
    Single<List<GetLimitOrdersV4Response>> getLimitOrdersByAddressRx(GetLimitOrdersRequest request);

    // Get Order by Order Hash
    GetLimitOrdersV4Response getOrderByOrderHash(Integer chainId, String orderHash) throws OneInchException;
    CompletableFuture<GetLimitOrdersV4Response> getOrderByOrderHashAsync(Integer chainId, String orderHash);
    Single<GetLimitOrdersV4Response> getOrderByOrderHashRx(Integer chainId, String orderHash);

    // Get All Limit Orders
    List<GetLimitOrdersV4Response> getAllLimitOrders(GetAllLimitOrdersRequest request) throws OneInchException;
    CompletableFuture<List<GetLimitOrdersV4Response>> getAllLimitOrdersAsync(GetAllLimitOrdersRequest request);
    Single<List<GetLimitOrdersV4Response>> getAllLimitOrdersRx(GetAllLimitOrdersRequest request);

    // Get Orders Count
    GetLimitOrdersCountV4Response getOrdersCount(GetLimitOrdersCountRequest request) throws OneInchException;
    CompletableFuture<GetLimitOrdersCountV4Response> getOrdersCountAsync(GetLimitOrdersCountRequest request);
    Single<GetLimitOrdersCountV4Response> getOrdersCountRx(GetLimitOrdersCountRequest request);

    // Get Events by Order Hash
    Map<String, List<GetEventsV4Response>> getEventsByOrderHash(Integer chainId, String orderHash) throws OneInchException;
    CompletableFuture<Map<String, List<GetEventsV4Response>>> getEventsByOrderHashAsync(Integer chainId, String orderHash);
    Single<Map<String, List<GetEventsV4Response>>> getEventsByOrderHashRx(Integer chainId, String orderHash);

    // Get All Events
    List<GetEventsV4Response> getAllEvents(GetEventsRequest request) throws OneInchException;
    CompletableFuture<List<GetEventsV4Response>> getAllEventsAsync(GetEventsRequest request);
    Single<List<GetEventsV4Response>> getAllEventsRx(GetEventsRequest request);

    // Has Active Orders with Permit
    GetHasActiveOrdersWithPermitV4Response hasActiveOrdersWithPermit(HasActiveOrdersWithPermitRequest request) throws OneInchException;
    CompletableFuture<GetHasActiveOrdersWithPermitV4Response> hasActiveOrdersWithPermitAsync(HasActiveOrdersWithPermitRequest request);
    Single<GetHasActiveOrdersWithPermitV4Response> hasActiveOrdersWithPermitRx(HasActiveOrdersWithPermitRequest request);

    // Get Unique Active Pairs
    GetActiveUniquePairsResponse getUniqueActivePairs(GetUniqueActivePairsRequest request) throws OneInchException;
    CompletableFuture<GetActiveUniquePairsResponse> getUniqueActivePairsAsync(GetUniqueActivePairsRequest request);
    Single<GetActiveUniquePairsResponse> getUniqueActivePairsRx(GetUniqueActivePairsRequest request);
}