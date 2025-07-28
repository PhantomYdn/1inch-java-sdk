package io.oneinch.sdk.client;

import io.oneinch.sdk.model.fusion.SignedOrderInput;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

/**
 * Retrofit API service interface for 1inch Fusion Relayer API.
 * Handles submission of signed orders to the Fusion system.
 */
public interface OneInchFusionRelayerApiService {
    
    /**
     * Submit a limit order that resolvers will be able to fill.
     * 
     * @param chainId Chain ID for the network
     * @param signedOrder Signed order input containing order data, signature, extension, and quoteId
     * @return Single indicating successful submission (no response body on success)
     */
    @POST("fusion/v2.0/{chainId}/relayer/order/submit")
    Single<Void> submitOrder(
            @Path("chainId") Integer chainId,
            @Body SignedOrderInput signedOrder
    );
    
    /**
     * Submit a list of limit orders which resolvers will be able to fill.
     * 
     * @param chainId Chain ID for the network
     * @param signedOrders List of signed orders to submit
     * @return Single indicating successful submission (no response body on success)
     */
    @POST("fusion/v2.0/{chainId}/relayer/order/submit/many")
    Single<Void> submitManyOrders(
            @Path("chainId") Integer chainId,
            @Body List<SignedOrderInput> signedOrders
    );
}