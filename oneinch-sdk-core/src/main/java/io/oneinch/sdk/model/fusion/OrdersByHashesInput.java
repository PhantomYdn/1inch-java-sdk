package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Input for batch order status requests by order hashes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdersByHashesInput {
    
    /**
     * List of order hashes to query.
     * Example: ["0x10ea5bd12b2d04566e175de24c2df41a058bf16df4af3eb2fb9bff38a9da98e9", 
     *           "0x20ea5bd12b2d04566e175de24c2df41a058bf16df4af3eb2fb9bff38a9da98e8"]
     */
    @JsonProperty("orderHashes")
    private List<String> orderHashes;
}