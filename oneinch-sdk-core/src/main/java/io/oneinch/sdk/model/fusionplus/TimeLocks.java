package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Time lock configuration for FusionPlus cross-chain operations.
 * Defines various time periods for withdrawal and cancellation actions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeLocks {

    /**
     * Time period for source chain withdrawal (in seconds)
     */
    @JsonProperty("srcWithdrawal")
    private Integer srcWithdrawal;

    /**
     * Time period for source chain public withdrawal (in seconds)
     */
    @JsonProperty("srcPublicWithdrawal")
    private Integer srcPublicWithdrawal;

    /**
     * Time period for source chain cancellation (in seconds)
     */
    @JsonProperty("srcCancellation")
    private Integer srcCancellation;

    /**
     * Time period for source chain public cancellation (in seconds)
     */
    @JsonProperty("srcPublicCancellation")
    private Integer srcPublicCancellation;

    /**
     * Time period for destination chain withdrawal (in seconds)
     */
    @JsonProperty("dstWithdrawal")
    private Integer dstWithdrawal;

    /**
     * Time period for destination chain public withdrawal (in seconds)
     */
    @JsonProperty("dstPublicWithdrawal")
    private Integer dstPublicWithdrawal;

    /**
     * Time period for destination chain cancellation (in seconds)
     */
    @JsonProperty("dstCancellation")
    private Integer dstCancellation;
}