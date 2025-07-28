package io.oneinch.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meta {
    private Long totalItems;
    private Integer itemsPerPage;
    private Integer totalPages;
    private Integer currentPage;
}