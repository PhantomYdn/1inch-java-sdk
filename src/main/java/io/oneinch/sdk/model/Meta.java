package io.oneinch.sdk.model;

import lombok.Data;

@Data
public class Meta {
    private Long totalItems;
    private Integer itemsPerPage;
    private Integer totalPages;
    private Integer currentPage;
}