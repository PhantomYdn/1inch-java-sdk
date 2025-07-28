package io.oneinch.sdk.model.fusionplus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Escrow factory configuration for FusionPlus cross-chain orders.
 * Defines the smart contract responsible for creating and managing escrows.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EscrowFactory {

    /**
     * Address of the escrow factory contract
     */
    @JsonProperty("factory")
    private String factory;

    /**
     * Bytecode hash of the escrow contract implementation
     */
    @JsonProperty("bytecodeHash")
    private String bytecodeHash;
}