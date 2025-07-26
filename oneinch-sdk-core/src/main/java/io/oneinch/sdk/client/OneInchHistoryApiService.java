package io.oneinch.sdk.client;

import io.oneinch.sdk.model.HistoryResponseDto;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OneInchHistoryApiService {

    /**
     * Returns history events for the specified address.
     *
     * @param address Account address
     * @param limit Amount of events to return (default is 100, max is 10000)
     * @param tokenAddress Token address used at event (optional)
     * @param chainId Chain ID (optional)
     * @param toTimestampMs To time in milliseconds (optional)
     * @param fromTimestampMs From time in milliseconds (optional)
     * @return Single containing history response with events
     */
    @GET("history/v2.0/history/{address}/events")
    Single<HistoryResponseDto> getHistoryEvents(
            @Path("address") String address,
            @Query("limit") Integer limit,
            @Query("tokenAddress") String tokenAddress,
            @Query("chainId") Integer chainId,
            @Query("toTimestampMs") String toTimestampMs,
            @Query("fromTimestampMs") String fromTimestampMs
    );
}