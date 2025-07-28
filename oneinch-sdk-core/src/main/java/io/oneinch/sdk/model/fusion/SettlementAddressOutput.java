package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing the current settlement contract address.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementAddressOutput {
    
    /**
     * Current settlement contract address.
     */
    @JsonProperty("address")
    private String address;
}