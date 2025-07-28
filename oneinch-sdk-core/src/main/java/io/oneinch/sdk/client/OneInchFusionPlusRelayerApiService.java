package io.oneinch.sdk.client;

import io.oneinch.sdk.model.fusionplus.FusionPlusSignedOrderInput;
import io.oneinch.sdk.model.fusionplus.SecretInput;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

import java.util.List;

/**
 * Retrofit API service interface for 1inch FusionPlus Relayer API.
 * Handles cross-chain order submission and secret management.
 */
public interface OneInchFusionPlusRelayerApiService {
    
    /**
     * Submit a single cross-chain order to the FusionPlus network.
     *
     * @param srcChainId Source chain ID where the order originates
     * @param signedOrder Signed cross-chain order with all required parameters
     * @return Single that completes when order is submitted successfully
     */
    @POST("relayer/v1.0/{srcChainId}/submit")
    Single<Void> submitOrder(
            @Path("srcChainId") Integer srcChainId,
            @Body FusionPlusSignedOrderInput signedOrder
    );
    
    /**
     * Submit multiple cross-chain orders to the FusionPlus network in batch.
     *
     * @param srcChainId Source chain ID where the orders originate
     * @param signedOrders List of signed cross-chain orders
     * @return Single that completes when all orders are submitted successfully
     */
    @POST("relayer/v1.0/{srcChainId}/submit/many")
    Single<Void> submitManyOrders(
            @Path("srcChainId") Integer srcChainId,
            @Body String signedOrders
    );
    
    /**
     * Submit secret for cross-chain order execution.
     * This is the second phase of the cross-chain atomic swap process.
     *
     * @param chainId Chain ID where the secret should be submitted
     * @param secretInput Secret information for order execution
     * @return Single that completes when secret is submitted successfully
     */
    @POST("relayer/v1.0/{chainId}/submit/secret")
    Single<Void> submitSecret(
            @Path("chainId") Integer chainId,
            @Body SecretInput secretInput
    );
}