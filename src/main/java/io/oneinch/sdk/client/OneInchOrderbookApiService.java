package io.oneinch.sdk.client;

import io.oneinch.sdk.model.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface OneInchOrderbookApiService {

    @POST("orderbook/v4.0/{chain}")
    Single<LimitOrderV4Response> createLimitOrder(
            @Path("chain") Integer chainId,
            @Body LimitOrderV4Request request
    );

    @GET("orderbook/v4.0/{chain}/address/{address}")
    Single<List<GetLimitOrdersV4Response>> getLimitOrdersByAddress(
            @Path("chain") Integer chainId,
            @Path("address") String address,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("statuses") String statuses,
            @Query("sortBy") String sortBy,
            @Query("takerAsset") String takerAsset,
            @Query("makerAsset") String makerAsset
    );

    @GET("orderbook/v4.0/{chain}/order/{orderHash}")
    Single<GetLimitOrdersV4Response> getOrderByOrderHash(
            @Path("chain") Integer chainId,
            @Path("orderHash") String orderHash
    );

    @GET("orderbook/v4.0/{chain}/all")
    Single<List<GetLimitOrdersV4Response>> getAllLimitOrders(
            @Path("chain") Integer chainId,
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("statuses") String statuses,
            @Query("sortBy") String sortBy,
            @Query("takerAsset") String takerAsset,
            @Query("makerAsset") String makerAsset
    );

    @GET("orderbook/v4.0/{chain}/count")
    Single<GetLimitOrdersCountV4Response> getOrdersCount(
            @Path("chain") Integer chainId,
            @Query("statuses") String statuses,
            @Query("takerAsset") String takerAsset,
            @Query("makerAsset") String makerAsset
    );

    @GET("orderbook/v4.0/{chain}/events/{orderHash}")
    Single<Map<String, List<GetEventsV4Response>>> getEventsByOrderHash(
            @Path("chain") Integer chainId,
            @Path("orderHash") String orderHash
    );

    @GET("orderbook/v4.0/{chain}/events")
    Single<List<GetEventsV4Response>> getAllEvents(
            @Path("chain") Integer chainId,
            @Query("limit") Integer limit
    );

    @GET("orderbook/v4.0/{chain}/has-active-orders-with-permit/{walletAddress}/{token}")
    Single<GetHasActiveOrdersWithPermitV4Response> hasActiveOrdersWithPermit(
            @Path("chain") Integer chainId,
            @Path("walletAddress") String walletAddress,
            @Path("token") String token
    );

    @GET("orderbook/v4.0/{chain}/unique-active-pairs")
    Single<GetActiveUniquePairsResponse> getUniqueActivePairs(
            @Path("chain") Integer chainId,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );
}