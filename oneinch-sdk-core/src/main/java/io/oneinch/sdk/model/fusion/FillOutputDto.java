package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object representing a fill of an order.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FillOutputDto {
    
    /**
     * Transaction hash where the fill occurred.
     * Example: 0x806039f5149065924ad52de616b50abff488c986716d052e9c160887bc09e559
     */
    @JsonProperty("txHash")
    private String txHash;
    
    /**
     * Amount of the makerAsset filled.
     * Example: 100000000000000000
     */
    @JsonProperty("filledMakerAmount")
    private String filledMakerAmount;
    
    /**
     * Amount of the takerAsset filled.
     * Example: 100000000000000000
     */
    @JsonProperty("filledAuctionTakerAmount")
    private String filledAuctionTakerAmount;
    
    /**
     * Total taker fee, including resolver and integrator.
     * Example: 123123
     */
    @JsonProperty("takerFeeAmount")
    private String takerFeeAmount;
}